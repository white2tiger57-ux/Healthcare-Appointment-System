package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.dto.ApiResponse;
import com.healthcare.appointmentsystem.entity.UserPreference;
import com.healthcare.appointmentsystem.repository.UserPreferenceRepository;
import com.healthcare.appointmentsystem.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile/preferences")
public class UserPreferenceController {

    private final UserPreferenceRepository repository;

    public UserPreferenceController(UserPreferenceRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> get(@AuthenticationPrincipal CustomUserDetails user) {
        var pref = repository.findByUserId(user.getId())
                .orElse(UserPreference.builder().userId(user.getId()).build());
        return ResponseEntity.ok(Map.of("success", true, "preferences", pref));
    }

    @PutMapping
    public ResponseEntity<ApiResponse> update(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal CustomUserDetails user) {
        var pref = repository.findByUserId(user.getId())
                .orElse(UserPreference.builder().userId(user.getId()).build());
        if (body.containsKey("emailNotifications"))
            pref.setEmailNotifications((Boolean) body.get("emailNotifications"));
        if (body.containsKey("smsNotifications"))
            pref.setSmsNotifications((Boolean) body.get("smsNotifications"));
        if (body.containsKey("appNotifications"))
            pref.setAppNotifications((Boolean) body.get("appNotifications"));
        if (body.containsKey("reminderTime"))
            pref.setReminderTime((String) body.get("reminderTime"));
        repository.save(pref);
        return ResponseEntity.ok(ApiResponse.success("Preferences updated"));
    }
}
