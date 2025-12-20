package com.testseries.service;

import com.testseries.model.*;
import com.testseries.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PracticeService {

    @Autowired
    private PracticeAttemptRepository practiceAttemptRepository;

    @Autowired
    private PracticeAnswerRepository practiceAnswerRepository;

    @Autowired
    private TestSeriesRepository testSeriesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Transactional
    public PracticeAttempt startPractice(Long userId, Long testSeriesId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        TestSeries testSeries = testSeriesRepository.findById(testSeriesId)
                .orElseThrow(() -> new RuntimeException("Test series not found"));

        PracticeAttempt attempt = new PracticeAttempt();
        attempt.setUser(user);
        attempt.setTestSeries(testSeries);
        attempt.setTotalQuestions(testSeries.getQuestions().size());
        attempt.setIsCompleted(false);
        
        return practiceAttemptRepository.save(attempt);
    }

    @Transactional
    public Map<String, Object> startCustomPractice(com.testseries.dto.TopicPracticeDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        PracticeAttempt attempt = new PracticeAttempt();
        attempt.setUser(user);
        attempt.setIsCompleted(false);

        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId()).orElse(null);
            attempt.setSubject(subject);
        }

        if (request.getTopicId() != null) {
            Topic topic = topicRepository.findById(request.getTopicId()).orElse(null);
            attempt.setTopic(topic);
        }

        List<Question> questions = questionRepository.findRandomQuestions(
                request.getSubjectId(),
                request.getTopicId(),
                request.getDifficulty(),
                request.getQuestionCount()
        );

        attempt.setTotalQuestions(questions.size());
        attempt = practiceAttemptRepository.save(attempt);

        Map<String, Object> response = new HashMap<>();
        response.put("attempt", attempt);
        response.put("questions", questions);

        return response;
    }

    @Transactional
    public Map<String, Object> submitAnswer(Long practiceAttemptId, Long questionId, String selectedAnswer, Integer timeSpent) {
        PracticeAttempt attempt = practiceAttemptRepository.findById(practiceAttemptId)
                .orElseThrow(() -> new RuntimeException("Practice attempt not found"));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // Check if already answered
        PracticeAnswer existingAnswer = practiceAnswerRepository
                .findByPracticeAttemptIdAndQuestionId(practiceAttemptId, questionId)
                .orElse(null);

        PracticeAnswer answer;
        if (existingAnswer != null) {
            answer = existingAnswer;
            answer.setSelectedAnswer(selectedAnswer);
            answer.setTimeSpentSeconds(timeSpent);
        } else {
            answer = new PracticeAnswer();
            answer.setPracticeAttempt(attempt);
            answer.setQuestion(question);
            answer.setSelectedAnswer(selectedAnswer);
            answer.setTimeSpentSeconds(timeSpent);
        }

        // Check if correct
        boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(selectedAnswer);
        answer.setIsCorrect(isCorrect);

        practiceAnswerRepository.save(answer);

        // Update attempt stats
        updateAttemptStats(attempt);

        // Return feedback
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("isCorrect", isCorrect);
        feedback.put("correctAnswer", question.getCorrectAnswer());
        feedback.put("explanation", question.getExplanation());
        feedback.put("selectedAnswer", selectedAnswer);
        
        return feedback;
    }

    private void updateAttemptStats(PracticeAttempt attempt) {
        List<PracticeAnswer> answers = practiceAnswerRepository.findByPracticeAttemptId(attempt.getId());
        
        int attempted = answers.size();
        long correct = answers.stream().filter(PracticeAnswer::getIsCorrect).count();
        double accuracy = attempted > 0 ? (correct * 100.0 / attempted) : 0.0;

        attempt.setQuestionsAttempted(attempted);
        attempt.setCorrectAnswers((int) correct);
        attempt.setAccuracy(accuracy);

        practiceAttemptRepository.save(attempt);
    }

    @Transactional
    public PracticeAttempt completePractice(Long practiceAttemptId) {
        PracticeAttempt attempt = practiceAttemptRepository.findById(practiceAttemptId)
                .orElseThrow(() -> new RuntimeException("Practice attempt not found"));

        attempt.setIsCompleted(true);
        attempt.setCompletedAt(LocalDateTime.now());
        
        return practiceAttemptRepository.save(attempt);
    }

    public List<PracticeAttempt> getUserPracticeHistory(Long userId) {
        return practiceAttemptRepository.findByUserIdOrderByStartedAtDesc(userId);
    }

    public PracticeAttempt getPracticeAttempt(Long practiceAttemptId, Long userId) {
        return practiceAttemptRepository.findByIdAndUserId(practiceAttemptId, userId)
                .orElseThrow(() -> new RuntimeException("Practice attempt not found"));
    }

    public List<PracticeAnswer> getPracticeAnswers(Long practiceAttemptId) {
        return practiceAnswerRepository.findByPracticeAttemptId(practiceAttemptId);
    }
}
