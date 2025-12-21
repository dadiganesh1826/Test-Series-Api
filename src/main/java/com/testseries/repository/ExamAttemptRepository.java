package com.testseries.repository;

import com.testseries.model.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    List<ExamAttempt> findByUserIdOrderByStartedAtDesc(Long userId);

    List<ExamAttempt> findByUserIdAndIsCompletedTrue(Long userId);

    List<ExamAttempt> findByUserIdAndStartedAtAfter(Long userId, java.time.LocalDateTime date);

    Optional<ExamAttempt> findByIdAndUserId(Long id, Long userId);

    List<ExamAttempt> findByTestSeriesIdAndIsCompletedTrue(Long testSeriesId);
    
    @org.springframework.data.jpa.repository.Query("SELECT new com.testseries.dto.LeaderboardEntryDTO(e.user.id, e.user.name, CAST(SUM(e.score) as int), CAST(COUNT(e) as int), 0, AVG(CAST(e.score as double) / e.totalMarks * 100)) " +
            "FROM ExamAttempt e WHERE e.isCompleted = true GROUP BY e.user.id, e.user.name ORDER BY SUM(e.score) DESC")
    List<com.testseries.dto.LeaderboardEntryDTO> findGlobalLeaderboard();

    @org.springframework.data.jpa.repository.Query("SELECT new com.testseries.dto.LeaderboardEntryDTO(e.user.id, e.user.name, e.score, 1, 0, CAST(e.score as double) / e.totalMarks * 100) " +
            "FROM ExamAttempt e WHERE e.testSeries.id = :testSeriesId AND e.isCompleted = true ORDER BY e.score DESC, e.timeSpentSeconds ASC")
    List<com.testseries.dto.LeaderboardEntryDTO> findTestLeaderboard(Long testSeriesId);

    @org.springframework.data.jpa.repository.Query("SELECT e.testSeries.title, COUNT(e), AVG(e.score) FROM ExamAttempt e WHERE e.isCompleted = true GROUP BY e.testSeries.title")
    List<Object[]> findTestPerformanceStats();
}
