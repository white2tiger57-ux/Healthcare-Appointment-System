package com.healthcare.appointmentsystem.repository;

import com.healthcare.appointmentsystem.entity.HealthMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HealthMetricRepository extends JpaRepository<HealthMetric, Long> {
    List<HealthMetric> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<HealthMetric> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}
