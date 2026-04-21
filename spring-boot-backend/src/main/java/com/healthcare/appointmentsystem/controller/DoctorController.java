package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.repository.AppointmentRepository;
import com.healthcare.appointmentsystem.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentRepository appointmentRepository;

    public DoctorController(DoctorService doctorService, AppointmentRepository appointmentRepository) {
        this.doctorService = doctorService;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/doctors")
    public ResponseEntity<Map<String, Object>> getDoctors(
            @RequestParam(name = "department_id", required = false) Long departmentId) {
        var doctors = doctorService.getDoctors(departmentId);
        return ResponseEntity.ok(Map.of("success", true, "count", doctors.size(), "doctors", doctors));
    }

    @GetMapping("/doctors/{doctorId}/availability")
    public ResponseEntity<Map<String, Object>> getAvailability(
            @PathVariable Long doctorId, @RequestParam String date) {
        return ResponseEntity.ok(doctorService.getAvailability(doctorId, date));
    }

    @GetMapping("/departments")
    public ResponseEntity<Map<String, Object>> getDepartments() {
        var departments = doctorService.getDepartments();
        return ResponseEntity.ok(Map.of("success", true, "count", departments.size(), "departments", departments));
    }

    @GetMapping("/doctors/{doctorId}/patients")
    public ResponseEntity<List<Map<String, Object>>> getDoctorPatients(@PathVariable Long doctorId) {
        var patients = appointmentRepository.findDistinctPatientsByDoctorId(doctorId);
        var result = patients.stream()
                .map(p -> Map.of("id", (Object) p.getId(), "name", (Object) p.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
