package com.testseries.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_activity_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String adminEmail;

    @Column(nullable = false)
    private String action; // e.g., "LOGIN", "BLOCK_USER", "CREATE_TEST"
    
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
