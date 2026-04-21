package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.dto.ApiResponse;
import com.healthcare.appointmentsystem.entity.Notification;
import com.healthcare.appointmentsystem.exception.ResourceNotFoundException;
import com.healthcare.appointmentsystem.repository.NotificationRepository;
import com.healthcare.appointmentsystem.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository repository;

    public NotificationController(NotificationRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String unread,
            @AuthenticationPrincipal CustomUserDetails user) {
        var notifications = "true".equals(unread)
                ? repository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId())
                : category != null
                    ? repository.findByUserIdAndCategoryOrderByCreatedAtDesc(user.getId(), category)
                    : repository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return ResponseEntity.ok(Map.of("success", true, "notifications", notifications));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse> markRead(
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
        Notification n = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        n.setIsRead(true);
        repository.save(n);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
        repository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Notification deleted"));
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<ApiResponse> deleteAll(
            @RequestParam(required = false) String category,
            @AuthenticationPrincipal CustomUserDetails user) {
        if (category != null) {
            repository.deleteByUserIdAndCategory(user.getId(), category);
        } else {
            repository.deleteByUserId(user.getId());
        }
        return ResponseEntity.ok(ApiResponse.success("Notifications deleted"));
    }
}
