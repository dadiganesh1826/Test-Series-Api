package com.testseries.repository;

import com.testseries.model.PracticeAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PracticeAttemptRepository extends JpaRepository<PracticeAttempt, Long> {
    List<PracticeAttempt> findByUserIdOrderByStartedAtDesc(Long userId);
    Optional<PracticeAttempt> findByIdAndUserId(Long id, Long userId);
    List<PracticeAttempt> findByUserIdAndIsCompletedFalse(Long userId);
}
