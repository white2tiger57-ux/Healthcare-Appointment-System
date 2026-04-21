package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.dto.ActivityRequest;
import com.healthcare.appointmentsystem.dto.ActivityResponse;
import com.healthcare.appointmentsystem.dto.ApiResponse;
import com.healthcare.appointmentsystem.security.CustomUserDetails;
import com.healthcare.appointmentsystem.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityResponse> createActivity(
            @Valid @RequestBody ActivityRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ActivityResponse response = activityService.createActivity(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityResponse> updateActivity(
            @PathVariable Long id,
            @Valid @RequestBody ActivityRequest request) {
        return ResponseEntity.ok(activityService.updateActivity(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.ok(ApiResponse.success("Activity deleted"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponse> getActivity(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.getActivity(id));
    }
}