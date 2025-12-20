package com.testseries.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
    
    // Emoji or image URL
    private String icon; 

    // E.g., "TESTS_COMPLETED", "HIGH_SCORE", "STREAK"
    @Column(name = "criteria_type")
    private String criteriaType; 

    // The threshold to unlock. E.g., 10 (for 10 tests) or 90 (for 90% score)
    @Column(name = "criteria_value")
    private Integer criteriaValue;
}
