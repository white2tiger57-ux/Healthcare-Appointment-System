package com.healthcare.appointmentsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Mobile_number", nullable = false)
    private String mobileNumber;

    @Column(name = "Age", nullable = false)
    private Integer age;

    @Column(name = "Gender", nullable = false)
    private String gender;

    @Column(name = "Blood_group", nullable = false)
    private String bloodGroup;

    @Column(name = "Weight", nullable = false)
    private Double weight;

    @Column(name = "Height")
    private Double height;

    @Column(name = "Medical_history", columnDefinition = "TEXT")
    private String medicalHistory;

    @Column(name = "Address", columnDefinition = "TEXT", nullable = false)
    private String address;

    @Column(name = "Email", nullable = false, unique = true)
    private String email;

    @Column(name = "Password")
    private String password;

    @Column(name = "Photo")
    private String photo;
}
