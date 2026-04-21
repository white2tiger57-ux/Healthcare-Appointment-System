package com.healthcare.appointmentsystem.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DoctorRegisterRequest {
    @NotBlank @Size(min = 2) private String name;
    @NotBlank @Pattern(regexp = "\\d{10,15}") private String contact;
    @NotBlank @Email private String email;
    @NotBlank private String qualification;
    @NotBlank private String specialization;
    @NotNull private Long departmentId;
    @NotBlank private String location;
    @NotBlank private String availability; // "Full-time" or "Part-time"
    @NotNull @Min(0) private Integer experience;
    @NotBlank @Size(min = 8) private String password;
    @NotBlank private String confirmPassword;
}
