package com.healthcare.appointmentsystem.service;

import com.healthcare.appointmentsystem.dto.AppointmentRequest;
import com.healthcare.appointmentsystem.dto.AppointmentResponse;
import com.healthcare.appointmentsystem.entity.Appointment;
import com.healthcare.appointmentsystem.entity.Doctor;
import com.healthcare.appointmentsystem.entity.Patient;
import com.healthcare.appointmentsystem.exception.DuplicateResourceException;
import com.healthcare.appointmentsystem.exception.ResourceNotFoundException;
import com.healthcare.appointmentsystem.repository.AppointmentRepository;
import com.healthcare.appointmentsystem.repository.DoctorRepository;
import com.healthcare.appointmentsystem.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private PatientRepository patientRepository;

    @InjectMocks private AppointmentService appointmentService;

    private Patient testPatient;
    private Doctor testDoctor;

    @BeforeEach
    void setUp() {
        testPatient = Patient.builder().id(1L).name("John Doe").email("john@test.com").build();
        testDoctor = Doctor.builder().id(1L).name("Dr. Smith").specialization("Cardiology")
                .departments(Collections.emptySet()).build();
    }

    @Test
    void bookAppointment_Success() {
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setDate("2025-06-15");
        request.setTime("09:00:00");
        request.setServiceType("Consultation");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.findConflicting(anyLong(), any(), any())).thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> {
            Appointment a = inv.getArgument(0);
            a.setId(100L);
            return a;
        });

        AppointmentResponse response = appointmentService.bookAppointment(request, 1L);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Scheduled", response.getStatus());
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_ConflictThrowsException() {
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setDate("2025-06-15");
        request.setTime("09:00:00");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.findConflicting(anyLong(), any(), any()))
                .thenReturn(List.of(new Appointment()));

        assertThrows(DuplicateResourceException.class,
                () -> appointmentService.bookAppointment(request, 1L));
    }

    @Test
    void bookAppointment_DoctorNotFound() {
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(999L);
        request.setDate("2025-06-15");
        request.setTime("09:00:00");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.bookAppointment(request, 1L));
    }

    @Test
    void cancelAppointment_Success() {
        Appointment appointment = Appointment.builder()
                .id(1L).patient(testPatient).doctor(testDoctor).status("Scheduled")
                .appointmentDate(LocalDate.now()).appointmentTime(LocalTime.of(9, 0))
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenReturn(appointment);

        AppointmentResponse response = appointmentService.cancelAppointment(1L, 1L);
        assertEquals("Cancelled", response.getStatus());
    }

    @Test
    void getByPatient_ReturnsAppointments() {
        Appointment a = Appointment.builder()
                .id(1L).patient(testPatient).doctor(testDoctor)
                .appointmentDate(LocalDate.now()).appointmentTime(LocalTime.of(10, 0))
                .status("Scheduled").build();

        when(appointmentRepository.findByPatientIdOrderByAppointmentDateAscAppointmentTimeAsc(1L))
                .thenReturn(List.of(a));

        List<AppointmentResponse> result = appointmentService.getByPatient(1L, null);
        assertEquals(1, result.size());
    }
}
