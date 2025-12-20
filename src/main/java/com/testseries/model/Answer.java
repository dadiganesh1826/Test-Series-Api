package com.testseries.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_attempt_id", nullable = false)
    @JsonIgnore
    private ExamAttempt examAttempt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "selected_answer")
    private String selectedAnswer; // A, B, C, or D

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "marks_obtained")
    private Integer marksObtained;

    @Column(name = "marked_for_review")
    private Boolean markedForReview = false;

    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds;
}
