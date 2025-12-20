package com.testseries.dto;

import lombok.Data;

@Data
public class TopicPracticeDTO {
    private Long userId;
    private Long subjectId;
    private Long topicId;
    private String difficulty;
    private Integer questionCount;
}
