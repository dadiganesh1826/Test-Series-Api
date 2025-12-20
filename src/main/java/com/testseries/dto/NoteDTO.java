package com.testseries.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoteDTO {
    private Long id;
    private Long userId;
    private Long questionId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
