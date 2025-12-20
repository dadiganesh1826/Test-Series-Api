package com.testseries.repository;

import com.testseries.model.Answer;
import com.testseries.model.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByExamAttemptId(Long examAttemptId);

    void deleteByExamAttempt(ExamAttempt examAttempt);
}
