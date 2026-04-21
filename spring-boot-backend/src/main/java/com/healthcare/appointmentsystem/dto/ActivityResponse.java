package com.healthcare.appointmentsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ActivityResponse {
    private Long id;
    private String action;
    private String detail;
    private Long timestamp;
    private UserInfo createdBy;
    private UserInfo handledBy;
    private String fileUrl;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String name;
        private String email;
    }
}