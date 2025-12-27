package com.testseries.repository;

import com.testseries.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.testseries.model.QuestionStatus;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByTestSeriesId(Long testSeriesId);

    @org.springframework.data.jpa.repository.Query("SELECT q FROM Question q WHERE q.topic.id = :topicId")
    List<Question> findByTopicId(Long topicId);

    List<Question> findByTestSeriesIsNull();

    List<Question> findByTestSeriesIsNullAndStatus(QuestionStatus status);

    List<Question> findByStatus(QuestionStatus status);

    @org.springframework.data.jpa.repository.Query(value = "SELECT * FROM questions q WHERE " +
            "(:subjectId IS NULL OR q.subject_id = :subjectId) AND " +
            "(:topicId IS NULL OR q.topic_id = :topicId) AND " +
            "(:difficulty IS NULL OR q.difficulty = :difficulty) AND " +
            "q.status = 'APPROVED' " +
            "ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestions(Long subjectId, Long topicId, String difficulty, int limit);
}
