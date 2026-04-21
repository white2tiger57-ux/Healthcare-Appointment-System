package com.healthcare.appointmentsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentRequest {
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotBlank(message = "Date is required")
    private String date; // yyyy-MM-dd

    @NotBlank(message = "Time is required")
    private String time; // HH:mm:ss

    private String serviceType;
    private String notes;
}
