package com.healthcare.appointmentsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private String specialization;
    private String departmentName;
    private String appointmentDate;
    private String appointmentTime;
    private String serviceType;
    private String notes;
    private String status;
    private String createdAt;
}
