package com.healthcare.appointmentsystem.service;

import com.healthcare.appointmentsystem.entity.DoctorSchedule;
import com.healthcare.appointmentsystem.entity.Appointment;
import com.healthcare.appointmentsystem.repository.AppointmentRepository;
import com.healthcare.appointmentsystem.repository.DoctorRepository;
import com.healthcare.appointmentsystem.repository.DoctorScheduleRepository;
import com.healthcare.appointmentsystem.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final DepartmentRepository departmentRepository;

    public DoctorService(DoctorRepository doctorRepository, DoctorScheduleRepository scheduleRepository,
                         AppointmentRepository appointmentRepository, DepartmentRepository departmentRepository) {
        this.doctorRepository = doctorRepository;
        this.scheduleRepository = scheduleRepository;
        this.appointmentRepository = appointmentRepository;
        this.departmentRepository = departmentRepository;
    }

    public List<Map<String, Object>> getDoctors(Long departmentId) {
        var doctors = departmentId != null
                ? doctorRepository.findByDepartmentId(departmentId)
                : doctorRepository.findAll();

        return doctors.stream().map(d -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("Doctor_ID", d.getId());
            map.put("Name", d.getName());
            map.put("Specialization", d.getSpecialization());
            map.put("Qualification", d.getQualification());
            map.put("Experience", d.getExperience());
            map.put("Contact", d.getContact());
            if (!d.getDepartments().isEmpty()) {
                map.put("Department_Name", d.getDepartments().iterator().next().getName());
            }
            return map;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> getAvailability(Long doctorId, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        int dayOfWeek = date.getDayOfWeek().getValue(); // 1=Mon, 7=Sun

        List<DoctorSchedule> schedules = scheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek);
        if (schedules.isEmpty()) {
            return Map.of("success", true, "availableSlots", List.of(), "message", "Doctor not available on this day");
        }

        List<Appointment> booked = appointmentRepository.findByDoctorIdAndDate(doctorId, date);
        Set<LocalTime> bookedTimes = booked.stream().map(Appointment::getAppointmentTime).collect(Collectors.toSet());

        DoctorSchedule schedule = schedules.get(0);
        List<Map<String, Object>> slots = new ArrayList<>();
        LocalTime current = schedule.getStartTime();

        while (current.isBefore(schedule.getEndTime())) {
            String timeStr = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            int hour = current.getHour() % 12 == 0 ? 12 : current.getHour() % 12;
            String display = hour + ":" + String.format("%02d", current.getMinute()) + (current.getHour() >= 12 ? " PM" : " AM");

            slots.add(Map.of(
                    "time", timeStr,
                    "display", display,
                    "available", !bookedTimes.contains(current)
            ));
            current = current.plusMinutes(30);
        }

        return Map.of("success", true, "availableSlots", slots, "doctorId", doctorId, "date", dateStr);
    }

    public List<Map<String, Object>> getDepartments() {
        return departmentRepository.findAllByOrderByNameAsc().stream()
                .map(d -> Map.<String, Object>of("Department_ID", d.getId(), "Name", d.getName()))
                .collect(Collectors.toList());
    }
}
