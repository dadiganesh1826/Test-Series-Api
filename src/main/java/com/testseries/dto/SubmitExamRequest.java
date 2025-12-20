package com.testseries.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitExamRequest {
    private Long examAttemptId;
    private List<AnswerSubmission> answers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerSubmission {
        private Long questionId;
        private String selectedAnswer; // A, B, C, or D
        private Boolean markedForReview;
        private Integer timeSpentSeconds;
    }
}
