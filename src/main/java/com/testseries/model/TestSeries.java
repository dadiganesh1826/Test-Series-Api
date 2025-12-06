package com.testseries.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "test_series")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestSeries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    @Column(name = "passing_marks", nullable = false)
    private Integer passingMarks;

    @OneToMany(mappedBy = "testSeries", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(length = 20)
    private String difficulty; // EASY, MEDIUM, HARD

    @Column(length = 500)
    private String tags;

    @Column(name = "featured")
    private Boolean featured = false;

    @Column(name = "featured_order")
    private Integer featuredOrder;

    @Column(length = 2000)
    private String instructions;

    @Column(name = "exam_pattern", length = 1000)
    private String examPattern;

    @Column(name = "marking_scheme", length = 1000)
    private String markingScheme;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
