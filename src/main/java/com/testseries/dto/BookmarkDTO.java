package com.testseries.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookmarkDTO {
    private Long id;
    private Long userId;
    private Long questionId;
    private String questionText;
    private String subject;
    private String topic;
    private LocalDateTime createdAt;
}
