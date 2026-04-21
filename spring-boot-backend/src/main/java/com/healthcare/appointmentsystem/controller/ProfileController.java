package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.dto.ApiResponse;
import com.healthcare.appointmentsystem.entity.*;
import com.healthcare.appointmentsystem.exception.ResourceNotFoundException;
import com.healthcare.appointmentsystem.repository.*;
import com.healthcare.appointmentsystem.security.CustomUserDetails;
import com.healthcare.appointmentsystem.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public ProfileController(PatientRepository patientRepository, DoctorRepository doctorRepository,
                             UserRepository userRepository, FileStorageService fileStorageService) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal CustomUserDetails user) {
        Map<String, Object> profile = new LinkedHashMap<>();
        if ("patient".equals(user.getUserType())) {
            Patient p = patientRepository.findById(user.getRelatedId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", user.getRelatedId()));
            profile.put("Name", p.getName());
            profile.put("Mobile_number", p.getMobileNumber());
            profile.put("Age", p.getAge());
            profile.put("Gender", p.getGender());
            profile.put("Blood_group", p.getBloodGroup());
            profile.put("Address", p.getAddress());
            profile.put("Email", p.getEmail());
            profile.put("Photo", p.getPhoto());
        } else {
            Doctor d = doctorRepository.findById(user.getRelatedId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", user.getRelatedId()));
            profile.put("Name", d.getName());
            profile.put("Contact", d.getContact());
            profile.put("Email", d.getEmail());
            profile.put("Qualification", d.getQualification());
            profile.put("Specialization", d.getSpecialization());
            profile.put("Experience", d.getExperience());
            profile.put("Photo", d.getPhoto());
        }
        return ResponseEntity.ok(Map.of("success", true, "userType", user.getUserType(), "profile", profile));
    }

    @PutMapping
    public ResponseEntity<ApiResponse> updateProfile(
            @RequestBody Map<String, Object> body, @AuthenticationPrincipal CustomUserDetails user) {
        if ("patient".equals(user.getUserType())) {
            Patient p = patientRepository.findById(user.getRelatedId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", user.getRelatedId()));
            if (body.containsKey("name")) p.setName((String) body.get("name"));
            if (body.containsKey("mobile_number")) p.setMobileNumber((String) body.get("mobile_number"));
            if (body.containsKey("age")) p.setAge((Integer) body.get("age"));
            if (body.containsKey("gender")) p.setGender((String) body.get("gender"));
            if (body.containsKey("address")) p.setAddress((String) body.get("address"));
            patientRepository.save(p);
        } else {
            Doctor d = doctorRepository.findById(user.getRelatedId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", user.getRelatedId()));
            if (body.containsKey("name")) d.setName((String) body.get("name"));
            if (body.containsKey("mobile_number")) d.setContact((String) body.get("mobile_number"));
            doctorRepository.save(d);
        }
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully"));
    }

    @PostMapping("/photo")
    public ResponseEntity<Map<String, Object>> uploadPhoto(
            @RequestParam("photo") MultipartFile photo, @AuthenticationPrincipal CustomUserDetails user) {
        String ext = photo.getOriginalFilename() != null ?
                photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf('.')) : ".jpg";
        String filename = user.getUserType() + "_" + user.getRelatedId() + ext;
        fileStorageService.saveUpload(photo, filename);
        String photoPath = "uploads/" + filename;

        if ("patient".equals(user.getUserType())) {
            Patient p = patientRepository.findById(user.getRelatedId()).orElseThrow();
            p.setPhoto(photoPath);
            patientRepository.save(p);
        } else {
            Doctor d = doctorRepository.findById(user.getRelatedId()).orElseThrow();
            d.setPhoto(photoPath);
            doctorRepository.save(d);
        }
        return ResponseEntity.ok(Map.of("success", true, "message", "Photo updated", "path", photoPath));
    }

    @GetMapping("/photo")
    public ResponseEntity<Map<String, Object>> getPhoto(@AuthenticationPrincipal CustomUserDetails user) {
        String photo = null;
        if ("patient".equals(user.getUserType())) {
            photo = patientRepository.findById(user.getRelatedId()).map(Patient::getPhoto).orElse(null);
        } else {
            photo = doctorRepository.findById(user.getRelatedId()).map(Doctor::getPhoto).orElse(null);
        }
        if (photo == null) {
            return ResponseEntity.status(404).body(Map.of("success", false, "error", "No photo found"));
        }
        return ResponseEntity.ok(Map.of("success", true, "url", photo));
    }
}
