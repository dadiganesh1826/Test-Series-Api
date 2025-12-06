package com.testseries.repository;

import com.testseries.model.QuestionStatus;
import com.testseries.model.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionStatusRepository extends JpaRepository<QuestionStatus, Long> {
    List<QuestionStatus> findByExamAttempt(ExamAttempt examAttempt);

    List<QuestionStatus> findByExamAttemptId(Long examAttemptId);
}
