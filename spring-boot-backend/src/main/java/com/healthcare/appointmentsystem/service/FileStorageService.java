package com.healthcare.appointmentsystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
    private final Path uploadDir;
    private final Path medicalRecordsDir;

    public FileStorageService(@Value("${app.file.upload-dir}") String uploadDir,
                              @Value("${app.file.medical-records-dir}") String medicalRecordsDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.medicalRecordsDir = Paths.get(medicalRecordsDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
            Files.createDirectories(this.medicalRecordsDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directories", e);
        }
    }

    public String saveUpload(MultipartFile file, String filename) {
        return saveFile(file, uploadDir, filename);
    }

    public String saveMedicalRecord(MultipartFile file, String filename) {
        return saveFile(file, medicalRecordsDir, filename);
    }

    private String saveFile(MultipartFile file, Path dir, String filename) {
        try {
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            log.info("File saved: {}", target);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + filename, e);
        }
    }

    public Resource loadAsResource(String filename, String type) {
        try {
            Path dir = "medical-records".equals(type) ? medicalRecordsDir : uploadDir;
            Path filePath = dir.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) return resource;
            throw new RuntimeException("File not found: " + filename);
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found: " + filename, e);
        }
    }

    public void deleteFile(String filename, String type) {
        try {
            Path dir = "medical-records".equals(type) ? medicalRecordsDir : uploadDir;
            Files.deleteIfExists(dir.resolve(filename));
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filename, e);
        }
    }
}
