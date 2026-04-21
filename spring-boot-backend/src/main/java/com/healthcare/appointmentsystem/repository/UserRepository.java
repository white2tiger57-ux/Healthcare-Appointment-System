package com.healthcare.appointmentsystem.repository;

import com.healthcare.appointmentsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Optional<User> findByRelatedIdAndUserType(Long relatedId, String userType);
}
