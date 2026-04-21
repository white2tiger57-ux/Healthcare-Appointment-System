package com.healthcare.appointmentsystem.repository;

import com.healthcare.appointmentsystem.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientIdOrderByRecordDateDesc(Long patientId);
    List<MedicalRecord> findByPatientIdAndRecordTypeOrderByRecordDateDesc(Long patientId, String recordType);
}
