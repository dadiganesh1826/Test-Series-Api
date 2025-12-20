package com.testseries.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoSaveRequest {
    private Long examAttemptId;
    private List<AnswerData> answers;
    private List<QuestionStatusDTO> questionStatuses;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerData {
        private Long questionId;
        private String selectedAnswer;
        private Boolean markedForReview;
        private Integer timeSpentSeconds;
    }
}
