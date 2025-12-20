package com.testseries.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    private Integer rank;
    private Double percentile;
    private Integer totalAttempts;
    private Double averageScore;
    private Double highestScore;
    private Map<String, SubjectPerformance> subjectPerformance;
    private List<TimeAnalysis> timeAnalysis;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectPerformance {
        private String subjectName;
        private Integer totalQuestions;
        private Integer correctAnswers;
        private Double accuracy;
        private List<TopicPerformance> topics;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicPerformance {
        private String topicName;
        private Integer correct;
        private Integer total;
        private Double accuracy;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeAnalysis {
        private Long questionId;
        private String questionText;
        private Integer timeSpentSeconds;
        private Integer averageTime;
        private Boolean tookTooLong;
    }
}
