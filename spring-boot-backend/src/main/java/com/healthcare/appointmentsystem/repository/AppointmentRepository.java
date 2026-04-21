package com.healthcare.appointmentsystem.repository;

import com.healthcare.appointmentsystem.entity.Appointment;
import com.healthcare.appointmentsystem.entity.Patient;
import com.healthcare.appointmentsystem.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.doctor.id = :doctorId")
    List<Patient> findDistinctPatientsByDoctorId(Long doctorId);

    @Query("SELECT DISTINCT a.doctor FROM Appointment a WHERE a.patient.id = :patientId")
    List<Doctor> findDistinctDoctorsByPatientId(@Param("patientId") Long patientId);

    List<Appointment> findByPatientIdOrderByAppointmentDateAscAppointmentTimeAsc(Long patientId);

    List<Appointment> findByDoctorIdOrderByAppointmentDateAscAppointmentTimeAsc(Long doctorId);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.status <> 'Cancelled'")
    List<Appointment> findByDoctorIdAndDate(Long doctorId, LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.appointmentTime = :time AND a.status <> 'Cancelled'")
    List<Appointment> findConflicting(Long doctorId, LocalDate date, LocalTime time);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND (a.appointmentDate > CURRENT_DATE OR (a.appointmentDate = CURRENT_DATE AND a.appointmentTime > CURRENT_TIME)) ORDER BY a.appointmentDate ASC")
    List<Appointment> findUpcomingByPatientId(Long patientId);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = CURRENT_DATE ORDER BY a.appointmentTime ASC")
    List<Appointment> findTodayByDoctorId(Long doctorId);

    long countByPatientId(Long patientId);
    long countByDoctorId(Long doctorId);
}
