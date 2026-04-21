package com.healthcare.appointmentsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "department")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
}
