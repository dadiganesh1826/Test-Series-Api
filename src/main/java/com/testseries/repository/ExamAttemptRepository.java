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
    Optional<ExamAttempt> findByIdAndUserId(Long id, Long userId);
}
