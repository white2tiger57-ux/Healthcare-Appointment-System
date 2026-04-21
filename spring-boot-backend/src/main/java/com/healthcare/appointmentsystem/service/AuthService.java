package com.healthcare.appointmentsystem.service;

import com.healthcare.appointmentsystem.dto.*;
import com.healthcare.appointmentsystem.entity.*;
import com.healthcare.appointmentsystem.exception.BadRequestException;
import com.healthcare.appointmentsystem.exception.DuplicateResourceException;
import com.healthcare.appointmentsystem.repository.*;
import com.healthcare.appointmentsystem.security.CustomUserDetails;
import com.healthcare.appointmentsystem.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Set;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorScheduleRepository scheduleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository,
                       RoleRepository roleRepository, PatientRepository patientRepository,
                       DoctorRepository doctorRepository, DepartmentRepository departmentRepository,
                       DoctorScheduleRepository scheduleRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
        this.scheduleRepository = scheduleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = tokenProvider.generateToken(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return AuthResponse.builder()
                .success(true)
                .token(token)
                .userType(userDetails.getUserType())
                .userId(userDetails.getId())
                .relatedId(userDetails.getRelatedId())
                .expiresIn(3600)
                .build();
    }

    @Transactional
    public ApiResponse registerPatient(PatientRegisterRequest req) {
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        Patient patient = Patient.builder()
                .name(req.getName()).mobileNumber(req.getMobileNumber())
                .age(req.getAge()).bloodGroup(req.getBloodGroup())
                .weight(req.getWeight()).gender(req.getGender())
                .address(req.getAddress()).height(req.getHeight())
                .medicalHistory(req.getMedicalHistory()).email(req.getEmail())
                .build();
        patient = patientRepository.save(patient);

        Role patientRole = roleRepository.findByName("ROLE_PATIENT")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_PATIENT").build()));

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .userType("patient")
                .relatedId(patient.getId())
                .roles(Set.of(patientRole))
                .build();
        userRepository.save(user);

        log.info("Patient registered: {}", patient.getId());
        return ApiResponse.success("Patient registered successfully", patient.getId());
    }

    @Transactional
    public ApiResponse registerDoctor(DoctorRegisterRequest req) {
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        if (doctorRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        Department dept = departmentRepository.findById(req.getDepartmentId())
                .orElseThrow(() -> new BadRequestException("Invalid department ID"));

        Doctor doctor = Doctor.builder()
                .name(req.getName()).contact(req.getContact()).email(req.getEmail())
                .qualification(req.getQualification()).specialization(req.getSpecialization())
                .location(req.getLocation()).experience(req.getExperience())
                .departments(Set.of(dept))
                .build();
        doctor = doctorRepository.save(doctor);

        // Create schedule (Mon-Fri)
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = "Full-time".equals(req.getAvailability()) ? LocalTime.of(17, 0) : LocalTime.of(13, 0);
        for (int day = 1; day <= 5; day++) {
            scheduleRepository.save(DoctorSchedule.builder()
                    .doctor(doctor).dayOfWeek(day).startTime(start).endTime(end).build());
        }

        Role doctorRole = roleRepository.findByName("ROLE_DOCTOR")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_DOCTOR").build()));

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .userType("doctor")
                .relatedId(doctor.getId())
                .roles(Set.of(doctorRole))
                .build();
        userRepository.save(user);

        log.info("Doctor registered: {}", doctor.getId());
        return ApiResponse.success("Doctor registered successfully", doctor.getId());
    }
}
