package com.testseries.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultResponse {
    private Long examAttemptId;
    private String testSeriesTitle;
    private Integer score;
    private Integer totalMarks;
    private Double percentage;
    private Boolean isPassed;
    private LocalDateTime submittedAt;
    private List<QuestionResult> questionResults;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResult {
        private Long questionId;
        private String questionText;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private String correctAnswer;
        private String selectedAnswer;
        private Boolean isCorrect;
        private Integer marksObtained;
        private Integer questionMarks;
        private String explanation;
    }
}
