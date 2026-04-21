package com.healthcare.appointmentsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @Builder @AllArgsConstructor
public class AuthResponse {
    private boolean success;
    private String token;
    private String userType;
    private Long userId;
    private Long relatedId;
    private long expiresIn;
}
