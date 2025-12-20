package com.testseries.service;

import com.testseries.dto.ExamResultResponse;
import com.testseries.dto.SubmitExamRequest;
import com.testseries.model.*;
import com.testseries.repository.AnswerRepository;
import com.testseries.repository.ExamAttemptRepository;
import com.testseries.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamAttemptRepository examAttemptRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final TestSeriesService testSeriesService;
    private final UserService userService;
    private final GamificationService gamificationService;
    private static final Logger log = LoggerFactory.getLogger(ExamService.class);

    @Transactional
    public ExamAttempt startExam(Long userId, Long testSeriesId) {
        log.info("Starting exam for userId: " + userId + ", testSeriesId: " + testSeriesId);

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (testSeriesId == null) {
            throw new IllegalArgumentException("Test Series ID cannot be null");
        }

        User user = userService.getUserById(userId);
        log.info("Found user: " + user.getName());

        TestSeries testSeries = testSeriesService.getTestSeriesById(testSeriesId);
        log.info("Found test series: " + testSeries.getTitle());

        ExamAttempt examAttempt = new ExamAttempt();
        examAttempt.setUser(user);
        examAttempt.setTestSeries(testSeries);
        examAttempt.setTotalMarks(testSeries.getTotalMarks());
        examAttempt.setIsCompleted(false);
        examAttempt.setStartTime(LocalDateTime.now());

        ExamAttempt saved = examAttemptRepository.save(examAttempt);
        log.info("Created exam attempt with ID: " + saved.getId());
        return saved;
    }

    @Transactional
    public ExamResultResponse submitExam(SubmitExamRequest request) {
        if (request.getExamAttemptId() == null) {
            throw new IllegalArgumentException("Exam attempt ID cannot be null");
        }
        log.info("Submitting exam for attempt ID: " + request.getExamAttemptId());
        ExamAttempt examAttempt = examAttemptRepository.findById(request.getExamAttemptId())
                .orElseThrow(() -> new RuntimeException("Exam attempt not found"));

        if (examAttempt.getIsCompleted()) {
            throw new RuntimeException("Exam already submitted");
        }

        int totalScore = 0;
        List<Answer> answers = new ArrayList<>();

        List<SubmitExamRequest.AnswerSubmission> submissions = request.getAnswers();
        if (submissions == null) {
            submissions = new ArrayList<>();
        }

        // Clear existing autosaved answers to avoid duplicates
        answerRepository.deleteByExamAttempt(examAttempt);

        for (SubmitExamRequest.AnswerSubmission submission : submissions) {
            if (submission.getQuestionId() == null) {
                continue;
            }
            Question question = questionRepository.findById(submission.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            Answer answer = new Answer();
            answer.setExamAttempt(examAttempt);
            answer.setQuestion(question);
            answer.setSelectedAnswer(submission.getSelectedAnswer());
            answer.setMarkedForReview(
                    submission.getMarkedForReview() != null ? submission.getMarkedForReview() : false);
            answer.setTimeSpentSeconds(submission.getTimeSpentSeconds());

            boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(submission.getSelectedAnswer());
            answer.setIsCorrect(isCorrect);
            answer.setMarksObtained(isCorrect ? question.getMarks() : 0);

            if (isCorrect) {
                totalScore += question.getMarks();
            }

            answers.add(answer);
        }

        // Don't call setAnswers() as it causes cascade="all-delete-orphan" issue
        // The answers are already linked to examAttempt via
        // answer.setExamAttempt(examAttempt)
        answerRepository.saveAll(answers);

        LocalDateTime endTime = LocalDateTime.now();
        examAttempt.setScore(totalScore);
        examAttempt.setSubmittedAt(endTime);
        examAttempt.setEndTime(endTime);
        examAttempt.setIsCompleted(true);
        examAttempt.setIsPassed(totalScore >= examAttempt.getTestSeries().getPassingMarks());

        // Calculate time spent in seconds
        if (examAttempt.getStartTime() != null) {
            long seconds = java.time.Duration.between(examAttempt.getStartTime(), endTime).getSeconds();
            examAttempt.setTimeSpentSeconds((int) seconds);
        }

        examAttemptRepository.save(examAttempt);
        
        // Check for badges
        try {
            gamificationService.checkForBadges(examAttempt.getUser().getId());
        } catch (Exception e) {
            log.error("Error checking badges: " + e.getMessage());
            // Don't fail the exam submission if gamification fails
        }

        return buildExamResultResponse(examAttempt);
    }

    public List<ExamAttempt> getUserExamHistory(Long userId) {
        return examAttemptRepository.findByUserIdOrderByStartedAtDesc(userId);
    }

    public ExamResultResponse getExamResult(Long examAttemptId, Long userId) {
        ExamAttempt examAttempt = examAttemptRepository.findByIdAndUserId(examAttemptId, userId)
                .orElseThrow(() -> new RuntimeException("Exam attempt not found"));

        if (!examAttempt.getIsCompleted()) {
            throw new RuntimeException("Exam not yet submitted");
        }

        return buildExamResultResponse(examAttempt);
    }

    private ExamResultResponse buildExamResultResponse(ExamAttempt examAttempt) {
        List<ExamResultResponse.QuestionResult> questionResults = examAttempt.getAnswers().stream()
                .map(answer -> {
                    Question q = answer.getQuestion();
                    boolean isCorrect = false;
                    Integer marksObtained = 0;
                    if (answer.getSelectedAnswer() != null) {
                        isCorrect = q.getCorrectAnswer().equalsIgnoreCase(answer.getSelectedAnswer());
                        marksObtained = isCorrect ? q.getMarks() : 0;
                    }

                    return new ExamResultResponse.QuestionResult(
                            q.getId(),
                            q.getQuestionText(),
                            q.getOptionA(),
                            q.getOptionB(),
                            q.getOptionC(),
                            q.getOptionD(),
                            q.getCorrectAnswer(),
                            answer.getSelectedAnswer(),
                            isCorrect,
                            marksObtained,
                            q.getMarks(),
                            q.getExplanation());
                })
                .collect(Collectors.toList());

        double percentage = (examAttempt.getScore() * 100.0) / examAttempt.getTotalMarks();

        return new ExamResultResponse(
                examAttempt.getId(),
                examAttempt.getTestSeries().getTitle(),
                examAttempt.getScore(),
                examAttempt.getTotalMarks(),
                percentage,
                examAttempt.getIsPassed(),
                examAttempt.getSubmittedAt(),
                questionResults);
    }

    @Transactional
    public void autoSaveExam(com.testseries.dto.AutoSaveRequest request) {
        ExamAttempt examAttempt = examAttemptRepository.findById(request.getExamAttemptId())
                .orElseThrow(() -> new RuntimeException("Exam attempt not found"));

        if (examAttempt.getIsCompleted()) {
            throw new RuntimeException("Exam already submitted");
        }

        // Delete existing answers for this attempt
        answerRepository.deleteByExamAttempt(examAttempt);

        // Save new answers
        List<Answer> answers = new ArrayList<>();
        for (com.testseries.dto.AutoSaveRequest.AnswerData answerData : request.getAnswers()) {
            if (answerData.getSelectedAnswer() != null && !answerData.getSelectedAnswer().isEmpty()) {
                Question question = questionRepository.findById(answerData.getQuestionId())
                        .orElseThrow(() -> new RuntimeException("Question not found"));

                Answer answer = new Answer();
                answer.setExamAttempt(examAttempt);
                answer.setQuestion(question);
                answer.setSelectedAnswer(answerData.getSelectedAnswer());
                answer.setMarkedForReview(
                        answerData.getMarkedForReview() != null ? answerData.getMarkedForReview() : false);
                answer.setTimeSpentSeconds(answerData.getTimeSpentSeconds());

                answers.add(answer);
            }
        }

        answerRepository.saveAll(answers);
    }

    public Map<String, Object> getTimeRemaining(Long examAttemptId) {
        ExamAttempt examAttempt = examAttemptRepository.findById(examAttemptId)
                .orElseThrow(() -> new RuntimeException("Exam attempt not found"));

        Map<String, Object> response = new java.util.HashMap<>();

        if (examAttempt.getStartTime() == null) {
            response.put("timeRemaining", examAttempt.getTestSeries().getDurationMinutes() * 60);
            response.put("expired", false);
            return response;
        }

        long elapsedSeconds = java.time.Duration.between(examAttempt.getStartTime(), LocalDateTime.now()).getSeconds();
        long totalSeconds = examAttempt.getTestSeries().getDurationMinutes() * 60L;
        long remainingSeconds = totalSeconds - elapsedSeconds;

        response.put("timeRemaining", Math.max(0, remainingSeconds));
        response.put("expired", remainingSeconds <= 0);
        response.put("startTime", examAttempt.getStartTime());
        response.put("durationMinutes", examAttempt.getTestSeries().getDurationMinutes());

        return response;
    }
}
