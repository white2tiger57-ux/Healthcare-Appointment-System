package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.dto.ApiResponse;
import com.healthcare.appointmentsystem.dto.AppointmentRequest;
import com.healthcare.appointmentsystem.dto.AppointmentResponse;
import com.healthcare.appointmentsystem.security.CustomUserDetails;
import com.healthcare.appointmentsystem.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> book(
            @Valid @RequestBody AppointmentRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        AppointmentResponse response = appointmentService.bookAppointment(request, user.getRelatedId());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "appointmentId", response.getId(),
                "message", "Appointment booked successfully"
        ));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) String filter,
            @AuthenticationPrincipal CustomUserDetails user) {
        List<AppointmentResponse> appointments;
        if ("patient".equals(user.getUserType())) {
            appointments = appointmentService.getByPatient(user.getRelatedId(), filter);
        } else {
            appointments = appointmentService.getByDoctor(user.getRelatedId(), filter);
        }
        return ResponseEntity.ok(Map.of(
                "success", true,
                "count", appointments.size(),
                "appointments", appointments
        ));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user) {
        appointmentService.cancelAppointment(id, user.getRelatedId());
        return ResponseEntity.ok(ApiResponse.success("Appointment cancelled"));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        appointmentService.updateStatus(id, body.get("status"));
        return ResponseEntity.ok(ApiResponse.success("Appointment status updated"));
    }
}
