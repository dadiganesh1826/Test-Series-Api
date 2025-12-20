package com.testseries.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exam_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_series_id", nullable = false)
    private TestSeries testSeries;

    @OneToMany(mappedBy = "examAttempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "score")
    private Integer score;

    @Column(name = "total_marks")
    private Integer totalMarks;

    @Column(name = "is_passed")
    private Boolean isPassed;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds;

    @Column(name = "auto_submitted")
    private Boolean autoSubmitted = false;

    @PrePersist
    protected void onCreate() {
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
    }
}
