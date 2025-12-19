package com.testseries.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardEntryDTO {
    private Long userId;
    private String userName;
    private Integer totalScore;
    private Integer testsTaken;
    private Integer rank;
    private Double averageAccuracy;
}
