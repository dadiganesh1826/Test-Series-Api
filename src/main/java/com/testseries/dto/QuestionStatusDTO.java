package com.testseries.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionStatusDTO {
    private Long questionId;
    private String status; // NOT_VISITED, ANSWERED, NOT_ANSWERED, MARKED_FOR_REVIEW
}
