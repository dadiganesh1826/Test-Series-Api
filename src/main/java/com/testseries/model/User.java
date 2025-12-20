package com.testseries.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Profile Fields
    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "target_exam", length = 100)
    private String targetExam;

    // Gamification Fields
    @Column(name = "current_streak")
    private Integer currentStreak = 0;

    @Column(name = "longest_streak")
    private Integer longestStreak = 0;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Column(name = "weekly_goal")
    private Integer weeklyGoal = 50;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role = Role.STUDENT;

    @Column(name = "is_blocked")
    private Boolean isBlocked = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (role == null) {
            role = Role.STUDENT;
        }
    }
}
