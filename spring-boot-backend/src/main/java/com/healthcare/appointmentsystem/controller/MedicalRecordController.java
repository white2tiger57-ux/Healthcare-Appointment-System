package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.dto.ApiResponse;
import com.healthcare.appointmentsystem.entity.MedicalRecord;
import com.healthcare.appointmentsystem.exception.ResourceNotFoundException;
import com.healthcare.appointmentsystem.repository.MedicalRecordRepository;
import com.healthcare.appointmentsystem.repository.PatientRepository;
import com.healthcare.appointmentsystem.security.CustomUserDetails;
import com.healthcare.appointmentsystem.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordRepository recordRepository;
    private final PatientRepository patientRepository;
    private final FileStorageService fileStorageService;

    public MedicalRecordController(MedicalRecordRepository recordRepository,
                                   PatientRepository patientRepository,
                                   FileStorageService fileStorageService) {
        this.recordRepository = recordRepository;
        this.patientRepository = patientRepository;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("recordType") String recordType,
            @RequestParam("date") String date,
            @RequestParam("description") String description,
            @RequestParam(value = "patientId", required = false) Long patientId,
            @AuthenticationPrincipal CustomUserDetails user) {

        String filename = "record_" + System.currentTimeMillis() +
                getExtension(Objects.requireNonNull(file.getOriginalFilename()));
        fileStorageService.saveMedicalRecord(file, filename);

        Long targetPatientId;
        if ("doctor".equals(user.getUserType())) {
            if (patientId == null) {
                throw new IllegalArgumentException("patientId is required when uploaded by a doctor");
            }
            if (!patientRepository.existsById(patientId)) {
                throw new ResourceNotFoundException("Patient not found with id: " + patientId);
            }
            targetPatientId = patientId;
        } else {
            targetPatientId = user.getRelatedId();
        }

        var patient = patientRepository.findById(targetPatientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", targetPatientId));

        MedicalRecord record = MedicalRecord.builder()
                .recordType(recordType)
                .patient(patient)
                .filePath("medical-records/" + filename)
                .description(description)
                .recordDate(LocalDate.parse(date))
                .build();

        if ("doctor".equals(user.getUserType())) {
            record.setDoctor(null); // Can be enhanced to set doctor reference if needed
        }

        record = recordRepository.save(record);
        return ResponseEntity.status(201).body(Map.of(
                "success", true, "recordId", record.getId(),
                "message", "Medical record uploaded successfully"));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) String type,
            @AuthenticationPrincipal CustomUserDetails user) {
        List<MedicalRecord> records = type != null
                ? recordRepository.findByPatientIdAndRecordTypeOrderByRecordDateDesc(user.getRelatedId(), type)
                : recordRepository.findByPatientIdOrderByRecordDateDesc(user.getRelatedId());

        var result = records.stream().map(r -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("record_id", r.getId());
            m.put("record_type", r.getRecordType());
            m.put("description", r.getDescription());
            m.put("file_path", r.getFilePath());
            m.put("record_date", r.getRecordDate() != null ? r.getRecordDate().toString() : null);
            m.put("created_at", r.getUploadedAt() != null ? r.getUploadedAt().toString() : null);
            m.put("doctor_name", r.getDoctor() != null ? r.getDoctor().getName() : null);
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("success", true, "records", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
        MedicalRecord r = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalRecord", "id", id));
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("record_id", r.getId());
        m.put("record_type", r.getRecordType());
        m.put("description", r.getDescription());
        m.put("file_path", r.getFilePath());
        m.put("record_date", r.getRecordDate() != null ? r.getRecordDate().toString() : null);
        return ResponseEntity.ok(Map.of("success", true, "record", m));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        MedicalRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalRecord", "id", id));
        String filename = Paths.get(record.getFilePath()).getFileName().toString();
        Resource resource = fileStorageService.loadAsResource(filename, "medical-records");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
        MedicalRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalRecord", "id", id));
        if (record.getFilePath() != null) {
            String filename = Paths.get(record.getFilePath()).getFileName().toString();
            fileStorageService.deleteFile(filename, "medical-records");
        }
        recordRepository.delete(record);
        return ResponseEntity.ok(ApiResponse.success("Medical record deleted"));
    }

    private String getExtension(String filename) {
        int i = filename.lastIndexOf('.');
        return i > 0 ? filename.substring(i) : "";
    }
}
