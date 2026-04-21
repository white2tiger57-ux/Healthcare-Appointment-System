package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.dto.ApiResponse;
import com.healthcare.appointmentsystem.entity.HealthMetric;
import com.healthcare.appointmentsystem.exception.BadRequestException;
import com.healthcare.appointmentsystem.repository.HealthMetricRepository;
import com.healthcare.appointmentsystem.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/health-metrics")
public class HealthMetricsController {

    private final HealthMetricRepository repository;

    public HealthMetricsController(HealthMetricRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> add(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal CustomUserDetails user) {
        int heartRate = (int) body.get("heart_rate");
        int systolic = (int) body.get("systolic");
        int diastolic = (int) body.get("diastolic");
        double temperature = ((Number) body.get("temperature")).doubleValue();

        if (heartRate < 40 || heartRate > 200) throw new BadRequestException("Heart rate must be 40-200 BPM");
        if (systolic < 70 || systolic > 200) throw new BadRequestException("Invalid systolic value");
        if (diastolic < 40 || diastolic > 120) throw new BadRequestException("Invalid diastolic value");

        HealthMetric metric = HealthMetric.builder()
                .userId(user.getId())
                .heartRate(heartRate).systolic(systolic).diastolic(diastolic)
                .temperature(temperature)
                .notes((String) body.getOrDefault("notes", null))
                .build();
        metric = repository.save(metric);

        return ResponseEntity.status(201).body(Map.of(
                "success", true, "message", "Health metrics saved", "id", metric.getId()));
    }

    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> getLatest(@AuthenticationPrincipal CustomUserDetails user) {
        var metric = repository.findFirstByUserIdOrderByCreatedAtDesc(user.getId());
        return ResponseEntity.ok(Map.of("success", true, "metrics", metric.orElse(null)));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(@AuthenticationPrincipal CustomUserDetails user) {
        var metrics = repository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return ResponseEntity.ok(Map.of("success", true, "metrics", metrics));
    }
}
