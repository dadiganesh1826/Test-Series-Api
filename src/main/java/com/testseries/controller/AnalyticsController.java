package com.testseries.controller;

import com.testseries.dto.AnalyticsResponse;
import com.testseries.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/exam/{examAttemptId}/rank")
    public ResponseEntity<Map<String, Integer>> getRank(@PathVariable Long examAttemptId) {
        Integer rank = analyticsService.calculateRank(examAttemptId);
        return ResponseEntity.ok(Map.of("rank", rank));
    }

    @GetMapping("/exam/{examAttemptId}/percentile")
    public ResponseEntity<Map<String, Double>> getPercentile(@PathVariable Long examAttemptId) {
        Double percentile = analyticsService.calculatePercentile(examAttemptId);
        return ResponseEntity.ok(Map.of("percentile", percentile));
    }

    @GetMapping("/test-series/{testSeriesId}/statistics")
    public ResponseEntity<Map<String, Object>> getTestStatistics(@PathVariable Long testSeriesId) {
        Map<String, Object> stats = analyticsService.getTestStatistics(testSeriesId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/exam/{examAttemptId}/detailed")
    public ResponseEntity<AnalyticsResponse> getDetailedAnalytics(@PathVariable Long examAttemptId) {
        AnalyticsResponse analytics = analyticsService.getDetailedAnalytics(examAttemptId);
        return ResponseEntity.ok(analytics);
    }
}
