package com.testseries.service;

import com.testseries.dto.LeaderboardEntryDTO;
import com.testseries.repository.ExamAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    public List<LeaderboardEntryDTO> getGlobalLeaderboard() {
        List<LeaderboardEntryDTO> leaderboard = examAttemptRepository.findGlobalLeaderboard();
        return assignRanks(leaderboard);
    }

    public List<LeaderboardEntryDTO> getTestLeaderboard(Long testSeriesId) {
        List<LeaderboardEntryDTO> leaderboard = examAttemptRepository.findTestLeaderboard(testSeriesId);
        return assignRanks(leaderboard);
    }

    private List<LeaderboardEntryDTO> assignRanks(List<LeaderboardEntryDTO> entries) {
        // Limit to top 50 for performance
        List<LeaderboardEntryDTO> topEntries = entries.stream()
                .limit(50)
                .collect(Collectors.toList());

        for (int i = 0; i < topEntries.size(); i++) {
            topEntries.get(i).setRank(i + 1);
        }
        return topEntries;
    }
}
