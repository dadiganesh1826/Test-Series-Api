package com.testseries.repository;

import com.testseries.model.PracticeAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PracticeAnswerRepository extends JpaRepository<PracticeAnswer, Long> {
    List<PracticeAnswer> findByPracticeAttemptId(Long practiceAttemptId);
    Optional<PracticeAnswer> findByPracticeAttemptIdAndQuestionId(Long practiceAttemptId, Long questionId);
}
