package com.healthcare.appointmentsystem.repository;

import com.healthcare.appointmentsystem.entity.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    List<DoctorSchedule> findByDoctorIdAndDayOfWeek(Long doctorId, Integer dayOfWeek);
    List<DoctorSchedule> findByDoctorId(Long doctorId);
    void deleteByDoctorId(Long doctorId);
}
