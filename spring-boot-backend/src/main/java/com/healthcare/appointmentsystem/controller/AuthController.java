package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.dto.*;
import com.healthcare.appointmentsystem.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register/patient")
    public ResponseEntity<ApiResponse> registerPatient(@Valid @RequestBody PatientRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerPatient(request));
    }

    @PostMapping("/register/doctor")
    public ResponseEntity<ApiResponse> registerDoctor(@Valid @RequestBody DoctorRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerDoctor(request));
    }
}
