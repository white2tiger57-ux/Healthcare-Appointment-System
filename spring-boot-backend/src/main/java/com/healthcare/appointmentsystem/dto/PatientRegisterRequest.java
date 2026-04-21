package com.healthcare.appointmentsystem.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PatientRegisterRequest {
    @NotBlank @Size(min = 2) private String name;
    @NotBlank @Pattern(regexp = "\\d{10,15}") private String mobileNumber;
    @NotNull @Min(0) @Max(120) private Integer age;
    private String bloodGroup;
    private Double weight;
    @NotBlank private String gender;
    private String address;
    private Double height;
    private String medicalHistory;
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 8) private String password;
    @NotBlank private String confirmPassword;
}
