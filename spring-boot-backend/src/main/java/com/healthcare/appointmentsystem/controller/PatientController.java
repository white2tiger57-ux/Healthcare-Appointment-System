package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.entity.Patient;
import com.healthcare.appointmentsystem.repository.PatientRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    @GetMapping
    public List<Map<String, Object>> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream()
                .map(p -> Map.of(
                        "id", (Object) p.getId(),
                        "name", (Object) p.getName()
                ))
                .collect(Collectors.toList());
    }
}
