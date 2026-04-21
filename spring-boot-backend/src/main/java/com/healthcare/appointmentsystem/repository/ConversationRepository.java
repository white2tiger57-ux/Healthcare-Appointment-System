package com.healthcare.appointmentsystem.repository;

import com.healthcare.appointmentsystem.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c WHERE (c.participant1Id = :userId OR c.participant2Id = :userId) ORDER BY c.lastMessageAt DESC")
    List<Conversation> findByUserId(Long userId);

    @Query("SELECT c FROM Conversation c WHERE (c.participant1Id = :user1 AND c.participant2Id = :user2) OR (c.participant1Id = :user2 AND c.participant2Id = :user1)")
    Optional<Conversation> findByParticipants(Long user1, Long user2);
}
