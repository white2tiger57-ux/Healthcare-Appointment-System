package com.healthcare.appointmentsystem.repository;

import com.healthcare.appointmentsystem.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    Optional<UserPreference> findByUserId(Long userId);
}
