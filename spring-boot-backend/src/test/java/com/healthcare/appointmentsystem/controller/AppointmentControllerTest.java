package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.dto.ApiResponse;
import com.healthcare.appointmentsystem.dto.AppointmentResponse;
import com.healthcare.appointmentsystem.security.CustomUserDetails;
import com.healthcare.appointmentsystem.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AppointmentControllerTest {

        @Mock
        private AppointmentService appointmentService;

        @InjectMocks
        private AppointmentController appointmentController;

        private CustomUserDetails mockUserDetails;

        @BeforeEach
        void setUp() {
                mockUserDetails = mock(CustomUserDetails.class);
        }

        @Test
        void testListAppointmentsForPatient() {
                when(mockUserDetails.getUserType()).thenReturn("patient");
                when(mockUserDetails.getRelatedId()).thenReturn(1L);

                List<AppointmentResponse> mockAppointments = List.of(
                        AppointmentResponse.builder().id(1L).status("Scheduled").build()
                );
                when(appointmentService.getByPatient(1L, null)).thenReturn(mockAppointments);

                ResponseEntity<Map<String, Object>> response = appointmentController.list(null, mockUserDetails);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                Map<String, Object> body = response.getBody();
                assertNotNull(body);
                assertEquals(true, body.get("success"));
                assertEquals(1, body.get("count"));
                assertNotNull(body.get("appointments"));
        }

        @Test
        void testCancelAppointment() {
                when(mockUserDetails.getRelatedId()).thenReturn(1L);

                AppointmentResponse mockResponse = AppointmentResponse.builder()
                        .id(100L)
                        .status("Cancelled")
                        .build();

                when(appointmentService.cancelAppointment(100L, 1L)).thenReturn(mockResponse);

                ResponseEntity<ApiResponse> response = appointmentController.cancel(100L, mockUserDetails);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("Appointment cancelled", response.getBody().getMessage());
                assertEquals(true, response.getBody().isSuccess());
        }
}