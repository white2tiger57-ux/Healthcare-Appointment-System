package com.healthcare.appointmentsystem.repository;

import com.healthcare.appointmentsystem.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByDoctorIdOrderByCreatedAtDesc(Long doctorId);
    List<Feedback> findTop10ByOrderByCreatedAtDesc();
}
