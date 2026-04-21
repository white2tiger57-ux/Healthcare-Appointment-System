package com.healthcare.appointmentsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = true) // was false
    private Doctor doctor;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "prescription", columnDefinition = "TEXT")
    private String prescription;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "record_type")
    private String recordType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        if (uploadedAt == null)
            uploadedAt = LocalDateTime.now();
    }
}
