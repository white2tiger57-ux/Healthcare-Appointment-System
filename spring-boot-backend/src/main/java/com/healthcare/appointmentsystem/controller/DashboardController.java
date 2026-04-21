package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.repository.*;
import com.healthcare.appointmentsystem.security.CustomUserDetails;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final HealthMetricRepository healthMetricRepository;
    private final NotificationRepository notificationRepository;

    public DashboardController(AppointmentRepository appointmentRepository,
                               MedicalRecordRepository medicalRecordRepository,
                               HealthMetricRepository healthMetricRepository,
                               NotificationRepository notificationRepository) {
        this.appointmentRepository = appointmentRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.healthMetricRepository = healthMetricRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/patient")
    public ResponseEntity<Map<String, Object>> patientDashboard(@AuthenticationPrincipal CustomUserDetails user) {
        var upcoming = appointmentRepository.findUpcomingByPatientId(user.getRelatedId());
        var records = medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(user.getRelatedId());
        var latestMetric = healthMetricRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId());
        var unreadNotifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId());
        long totalAppointments = appointmentRepository.countByPatientId(user.getRelatedId());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("upcomingAppointments", upcoming.size() > 5 ? upcoming.subList(0, 5) : upcoming);
        data.put("recentRecords", records.size() > 5 ? records.subList(0, 5) : records);
        data.put("latestHealthMetric", latestMetric.orElse(null));
        data.put("unreadNotifications", unreadNotifications.size());
        data.put("totalAppointments", totalAppointments);
        data.put("totalRecords", records.size());

        return ResponseEntity.ok(Map.of("success", true, "dashboard", data));
    }

    @GetMapping("/doctor")
    @Cacheable(value = "doctorDashboard", key = "#user.relatedId")
    public ResponseEntity<Map<String, Object>> doctorDashboard(@AuthenticationPrincipal CustomUserDetails user) {
        var todayAppointments = appointmentRepository.findTodayByDoctorId(user.getRelatedId());
        var allAppointments = appointmentRepository.findByDoctorIdOrderByAppointmentDateAscAppointmentTimeAsc(user.getRelatedId());
        var unreadNotifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId());
        long totalAppointments = appointmentRepository.countByDoctorId(user.getRelatedId());

        long completedCount = allAppointments.stream().filter(a -> "Completed".equals(a.getStatus())).count();
        long scheduledCount = allAppointments.stream().filter(a -> "Scheduled".equals(a.getStatus())).count();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("todayAppointments", todayAppointments);
        data.put("totalAppointments", totalAppointments);
        data.put("completedAppointments", completedCount);
        data.put("scheduledAppointments", scheduledCount);
        data.put("unreadNotifications", unreadNotifications.size());

        return ResponseEntity.ok(Map.of("success", true, "dashboard", data));
    }
}
