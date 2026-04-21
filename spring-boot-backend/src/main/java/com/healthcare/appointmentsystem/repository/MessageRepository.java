package com.healthcare.appointmentsystem.repository;

import com.healthcare.appointmentsystem.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE (m.senderId = :user1 AND m.receiverId = :user2) OR (m.senderId = :user2 AND m.receiverId = :user1) ORDER BY m.createdAt ASC")
    List<Message> findConversationMessages(Long user1, Long user2);

    long countByReceiverIdAndIsReadFalse(Long receiverId);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE m.receiverId = :receiverId AND m.senderId = :senderId AND m.isRead = false")
    void markAsRead(Long receiverId, Long senderId);
}
