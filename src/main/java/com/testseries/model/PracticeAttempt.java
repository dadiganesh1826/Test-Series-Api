package com.testseries.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "practice_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticeAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_series_id", nullable = true)
    private TestSeries testSeries;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "questions_attempted")
    private Integer questionsAttempted = 0;

    @Column(name = "correct_answers")
    private Integer correctAnswers = 0;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "accuracy")
    private Double accuracy = 0.0;

    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
    }
}
