package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.entity.Feedback;
import com.healthcare.appointmentsystem.exception.BadRequestException;
import com.healthcare.appointmentsystem.repository.FeedbackRepository;
import com.healthcare.appointmentsystem.repository.PatientRepository;
import com.healthcare.appointmentsystem.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;
    private final PatientRepository patientRepository;

    public FeedbackController(FeedbackRepository feedbackRepository, PatientRepository patientRepository) {
        this.feedbackRepository = feedbackRepository;
        this.patientRepository = patientRepository;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> submit(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal CustomUserDetails user) {
        int rating = (int) body.get("rating");
        if (rating < 1 || rating > 5) throw new BadRequestException("Rating must be 1-5");

        Feedback feedback = Feedback.builder()
                .patientId(Long.valueOf(body.get("patientId").toString()))
                .doctorId(body.get("doctorId") != null ? Long.valueOf(body.get("doctorId").toString()) : null)
                .rating(rating)
                .comment((String) body.get("comment"))
                .feedbackType((String) body.get("feedbackType"))
                .isAnonymous(Boolean.TRUE.equals(body.get("isAnonymous")))
                .build();
        feedback = feedbackRepository.save(feedback);

        return ResponseEntity.status(201).body(Map.of(
                "success", true, "message", "Feedback submitted", "feedbackId", feedback.getId()));
    }

    @GetMapping("/recent")
    public ResponseEntity<Map<String, Object>> getRecent() {
        var feedbacks = feedbackRepository.findTop10ByOrderByCreatedAtDesc().stream().map(f -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", f.getId());
            m.put("rating", f.getRating());
            m.put("comment", f.getComment());
            m.put("feedbackType", f.getFeedbackType());
            m.put("isAnonymous", f.getIsAnonymous());
            m.put("createdAt", f.getCreatedAt());
            if (!Boolean.TRUE.equals(f.getIsAnonymous())) {
                patientRepository.findById(f.getPatientId())
                        .ifPresent(p -> m.put("patientName", p.getName()));
            }
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("success", true, "feedbacks", feedbacks));
    }
}
