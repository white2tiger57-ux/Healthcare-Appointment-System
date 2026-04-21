package com.healthcare.appointmentsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_metrics")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HealthMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "systolic")
    private Integer systolic;

    @Column(name = "diastolic")
    private Integer diastolic;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
