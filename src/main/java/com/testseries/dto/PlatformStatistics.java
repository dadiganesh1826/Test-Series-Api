package com.testseries.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformStatistics {
    private Long totalUsers;
    private Long totalTests;
    private Long totalAttempts;
    private Double averageSuccessRate;
}
