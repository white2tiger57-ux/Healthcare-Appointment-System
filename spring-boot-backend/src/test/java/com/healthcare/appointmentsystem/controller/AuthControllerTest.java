package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.dto.*;
import com.healthcare.appointmentsystem.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@healthcare.com");
        loginRequest.setPassword("password123");

        AuthResponse authResponse = AuthResponse.builder()
                .success(true)
                .token("dummy-jwt-token")
                .userType("admin")
                .userId(1L)
                .relatedId(0L)
                .expiresIn(3600)
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("dummy-jwt-token"))
                .andExpect(jsonPath("$.userType").value("admin"));
    }

    @Test
    void testRegisterPatient() throws Exception {
        PatientRegisterRequest request = new PatientRegisterRequest();
        request.setName("Test Patient");
        request.setEmail("test@patient.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");
        request.setMobileNumber("9876543210");
        request.setAge(30);
        request.setGender("Male");
        request.setAddress("123 Test St");
        request.setBloodGroup("O+");
        request.setWeight(70.0);

        when(authService.registerPatient(any(PatientRegisterRequest.class)))
                .thenReturn(ApiResponse.success("Patient registered successfully"));

        mockMvc.perform(post("/api/auth/register/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Patient registered successfully"));
    }
}