package com.healthcare.appointmentsystem.dto;

import lombok.Data;

@Data
public class ActivityRequest {
    private String action;
    private String detail;
    private Long handledById;  // ID of the user handling the task
}