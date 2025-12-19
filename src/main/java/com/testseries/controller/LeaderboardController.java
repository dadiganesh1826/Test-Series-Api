package com.testseries.controller;

import com.testseries.dto.LeaderboardEntryDTO;
import com.testseries.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "*")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping("/global")
    public ResponseEntity<List<LeaderboardEntryDTO>> getGlobalLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard());
    }

    @GetMapping("/test/{testSeriesId}")
    public ResponseEntity<List<LeaderboardEntryDTO>> getTestLeaderboard(@PathVariable Long testSeriesId) {
        return ResponseEntity.ok(leaderboardService.getTestLeaderboard(testSeriesId));
    }
}
