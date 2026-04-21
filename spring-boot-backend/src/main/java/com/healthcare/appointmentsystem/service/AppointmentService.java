package com.healthcare.appointmentsystem.service;

import com.healthcare.appointmentsystem.dto.AppointmentRequest;
import com.healthcare.appointmentsystem.dto.AppointmentResponse;
import com.healthcare.appointmentsystem.entity.Appointment;
import com.healthcare.appointmentsystem.entity.Doctor;
import com.healthcare.appointmentsystem.entity.Patient;
import com.healthcare.appointmentsystem.exception.BadRequestException;
import com.healthcare.appointmentsystem.exception.DuplicateResourceException;
import com.healthcare.appointmentsystem.exception.ResourceNotFoundException;
import com.healthcare.appointmentsystem.repository.AppointmentRepository;
import com.healthcare.appointmentsystem.repository.DoctorRepository;
import com.healthcare.appointmentsystem.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public AppointmentResponse bookAppointment(AppointmentRequest req, Long patientId) {
        log.info("Booking appointment for patient {} with doctor {}", patientId, req.getDoctorId());

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));
        Doctor doctor = doctorRepository.findById(req.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", req.getDoctorId()));

        LocalDate date = LocalDate.parse(req.getDate());
        LocalTime time = LocalTime.parse(req.getTime());

        // Check for conflicts
        List<Appointment> conflicts = appointmentRepository.findConflicting(doctor.getId(), date, time);
        if (!conflicts.isEmpty()) {
            throw new DuplicateResourceException("Time slot no longer available");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient).doctor(doctor)
                .appointmentDate(date).appointmentTime(time)
                .serviceType(req.getServiceType()).notes(req.getNotes())
                .status("Scheduled")
                .build();

        appointment = appointmentRepository.save(appointment);
        log.info("Appointment booked: {}", appointment.getId());
        return mapToResponse(appointment);
    }

    public List<AppointmentResponse> getByPatient(Long patientId, String filter) {
        List<Appointment> appointments;
        if ("upcoming".equals(filter)) {
            appointments = appointmentRepository.findUpcomingByPatientId(patientId);
        } else {
            appointments = appointmentRepository.findByPatientIdOrderByAppointmentDateAscAppointmentTimeAsc(patientId);
        }
        return appointments.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<AppointmentResponse> getByDoctor(Long doctorId, String filter) {
        List<Appointment> appointments;
        if ("today".equals(filter)) {
            appointments = appointmentRepository.findTodayByDoctorId(doctorId);
        } else {
            appointments = appointmentRepository.findByDoctorIdOrderByAppointmentDateAscAppointmentTimeAsc(doctorId);
        }
        return appointments.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        if (!appointment.getPatient().getId().equals(userId) && !appointment.getDoctor().getId().equals(userId)) {
            throw new BadRequestException("Not authorized to cancel this appointment");
        }

        appointment.setStatus("Cancelled");
        appointment = appointmentRepository.save(appointment);
        log.info("Appointment cancelled: {}", appointmentId);
        return mapToResponse(appointment);
    }

    @Transactional
    public AppointmentResponse updateStatus(Long appointmentId, String status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));
        appointment.setStatus(status);
        return mapToResponse(appointmentRepository.save(appointment));
    }

    private AppointmentResponse mapToResponse(Appointment a) {
        String deptName = "";
        if (a.getDoctor().getDepartments() != null && !a.getDoctor().getDepartments().isEmpty()) {
            deptName = a.getDoctor().getDepartments().iterator().next().getName();
        }
        return AppointmentResponse.builder()
                .id(a.getId())
                .patientId(a.getPatient().getId())
                .patientName(a.getPatient().getName())
                .doctorId(a.getDoctor().getId())
                .doctorName(a.getDoctor().getName())
                .specialization(a.getDoctor().getSpecialization())
                .departmentName(deptName)
                .appointmentDate(a.getAppointmentDate().toString())
                .appointmentTime(a.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .serviceType(a.getServiceType())
                .notes(a.getNotes())
                .status(a.getStatus())
                .createdAt(a.getCreatedAt() != null ? a.getCreatedAt().toString() : null)
                .build();
    }
}
