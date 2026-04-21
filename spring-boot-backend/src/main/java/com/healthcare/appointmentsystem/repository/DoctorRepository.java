package com.healthcare.appointmentsystem.repository;

import com.healthcare.appointmentsystem.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);
    Boolean existsByEmail(String email);

    @Query("SELECT d FROM Doctor d JOIN d.departments dep WHERE dep.id = :departmentId ORDER BY d.name ASC")
    List<Doctor> findByDepartmentId(Long departmentId);
}
