package com.healthcare.appointmentsystem.repository;

import com.healthcare.appointmentsystem.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
}