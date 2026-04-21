package com.healthcare.appointmentsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "doctors")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Contact", nullable = false)
    private String contact;

    @Column(name = "Qualification", columnDefinition = "TEXT", nullable = false)
    private String qualification;

    @Column(name = "Specialization", columnDefinition = "TEXT", nullable = false)
    private String specialization;

    @Column(name = "Availability")
    private String availability;

    @Column(name = "Location", columnDefinition = "TEXT", nullable = false)
    private String location;

    @Column(name = "Email", nullable = false, unique = true)
    private String email;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "Experience")
    private Integer experience;

    @Column(name = "Photo")
    private String photo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "doctor_department",
        joinColumns = @JoinColumn(name = "doctor_id"),
        inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    @Builder.Default
    private Set<Department> departments = new HashSet<>();
}
