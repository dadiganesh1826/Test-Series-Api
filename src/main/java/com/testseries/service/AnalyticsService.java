package com.testseries.service;

import com.testseries.dto.AnalyticsResponse;
import com.testseries.model.*;
import com.testseries.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ExamAttemptRepository examAttemptRepository;
    private final AnswerRepository answerRepository;
    private final SubjectRepository subjectRepository;

    public Integer calculateRank(Long examAttemptId) {
        ExamAttempt attempt = examAttemptRepository.findById(examAttemptId)
                .orElseThrow(() -> new RuntimeException("Exam attempt not found"));

        // Get all attempts for the same test series
        List<ExamAttempt> allAttempts = examAttemptRepository
                .findByTestSeriesIdAndIsCompletedTrue(attempt.getTestSeries().getId());

        // Count how many scored higher
        long higherScores = allAttempts.stream()
                .filter(a -> a.getScore() > attempt.getScore())
                .count();

        return (int) (higherScores + 1); // +1 because rank starts from 1
    }

    public Double calculatePercentile(Long examAttemptId) {
        ExamAttempt attempt = examAttemptRepository.findById(examAttemptId)
                .orElseThrow(() -> new RuntimeException("Exam attempt not found"));

        List<ExamAttempt> allAttempts = examAttemptRepository
                .findByTestSeriesIdAndIsCompletedTrue(attempt.getTestSeries().getId());

        if (allAttempts.size() <= 1) {
            return 100.0;
        }

        // Count how many scored less than current score
        long lowerScores = allAttempts.stream()
                .filter(a -> a.getScore() < attempt.getScore())
                .count();

        return (lowerScores * 100.0) / allAttempts.size();
    }

    public Map<String, Object> getTestStatistics(Long testSeriesId) {
        List<ExamAttempt> attempts = examAttemptRepository
                .findByTestSeriesIdAndIsCompletedTrue(testSeriesId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAttempts", attempts.size());

        if (!attempts.isEmpty()) {
            double avgScore = attempts.stream()
                    .mapToInt(ExamAttempt::getScore)
                    .average()
                    .orElse(0.0);

            int highestScore = attempts.stream()
                    .mapToInt(ExamAttempt::getScore)
                    .max()
                    .orElse(0);

            int lowestScore = attempts.stream()
                    .mapToInt(ExamAttempt::getScore)
                    .min()
                    .orElse(0);

            stats.put("averageScore", avgScore);
            stats.put("highestScore", highestScore);
            stats.put("lowestScore", lowestScore);
        } else {
            stats.put("averageScore", 0.0);
            stats.put("highestScore", 0);
            stats.put("lowestScore", 0);
        }

        return stats;
    }

    public AnalyticsResponse getDetailedAnalytics(Long examAttemptId) {
        ExamAttempt attempt = examAttemptRepository.findById(examAttemptId)
                .orElseThrow(() -> new RuntimeException("Exam attempt not found"));

        AnalyticsResponse response = new AnalyticsResponse();

        // Calculate rank and percentile
        response.setRank(calculateRank(examAttemptId));
        response.setPercentile(calculatePercentile(examAttemptId));

        // Get test statistics
        Map<String, Object> stats = getTestStatistics(attempt.getTestSeries().getId());
        response.setTotalAttempts((Integer) stats.get("totalAttempts"));
        response.setAverageScore((Double) stats.get("averageScore"));
        response.setHighestScore((Double) stats.get("highestScore"));

        // Subject-wise performance
        response.setSubjectPerformance(calculateSubjectPerformance(attempt));

        // Time analysis
        response.setTimeAnalysis(calculateTimeAnalysis(attempt));

        return response;
    }

    private Map<String, AnalyticsResponse.SubjectPerformance> calculateSubjectPerformance(ExamAttempt attempt) {
        Map<String, AnalyticsResponse.SubjectPerformance> subjectMap = new HashMap<>();

        List<Answer> answers = attempt.getAnswers();

        // Group answers by subject
        Map<Subject, List<Answer>> answersBySubject = answers.stream()
                .filter(a -> a.getQuestion().getSubject() != null)
                .collect(Collectors.groupingBy(a -> a.getQuestion().getSubject()));

        for (Map.Entry<Subject, List<Answer>> entry : answersBySubject.entrySet()) {
            Subject subject = entry.getKey();
            List<Answer> subjectAnswers = entry.getValue();

            long correctCount = subjectAnswers.stream()
                    .filter(a -> Boolean.TRUE.equals(a.getIsCorrect()))
                    .count();

            double accuracy = (correctCount * 100.0) / subjectAnswers.size();

            AnalyticsResponse.SubjectPerformance performance = new AnalyticsResponse.SubjectPerformance();
            performance.setSubjectName(subject.getName());
            performance.setTotalQuestions(subjectAnswers.size());
            performance.setCorrectAnswers((int) correctCount);
            performance.setAccuracy(accuracy);
            performance.setTopics(new ArrayList<>()); // Can be expanded later

            subjectMap.put(subject.getName(), performance);
        }

        return subjectMap;
    }

    private List<AnalyticsResponse.TimeAnalysis> calculateTimeAnalysis(ExamAttempt attempt) {
        List<AnalyticsResponse.TimeAnalysis> timeAnalysisList = new ArrayList<>();

        List<Answer> answers = attempt.getAnswers();

        // Calculate average time
        int totalTime = answers.stream()
                .filter(a -> a.getTimeSpentSeconds() != null)
                .mapToInt(Answer::getTimeSpentSeconds)
                .sum();

        int avgTime = answers.isEmpty() ? 0 : totalTime / answers.size();

        for (Answer answer : answers) {
            if (answer.getTimeSpentSeconds() != null) {
                AnalyticsResponse.TimeAnalysis analysis = new AnalyticsResponse.TimeAnalysis();
                analysis.setQuestionId(answer.getQuestion().getId());
                analysis.setQuestionText(answer.getQuestion().getQuestionText());
                analysis.setTimeSpentSeconds(answer.getTimeSpentSeconds());
                analysis.setAverageTime(avgTime);
                analysis.setTookTooLong(answer.getTimeSpentSeconds() > avgTime * 1.5);

                timeAnalysisList.add(analysis);
            }
        }

        return timeAnalysisList;
    }
}
