package com.healthcare.appointmentsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "activities")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action;   // NOT_STARTED, IN_PROGRESS, COMPLETED

    @Column(columnDefinition = "TEXT")
    private String detail;

    private Long timestamp;  // epoch milliseconds

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handled_by")
    private User handledBy;

    private String fileUrl;

    // Optional: link to a project (if needed)
    // @ManyToOne private Project project;
}