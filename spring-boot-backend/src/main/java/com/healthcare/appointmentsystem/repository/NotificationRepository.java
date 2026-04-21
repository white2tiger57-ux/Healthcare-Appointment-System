package com.healthcare.appointmentsystem.repository;

import com.healthcare.appointmentsystem.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserIdAndCategoryOrderByCreatedAtDesc(Long userId, String category);
    void deleteByUserId(Long userId);
    void deleteByUserIdAndCategory(Long userId, String category);
}
