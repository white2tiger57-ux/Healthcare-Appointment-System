package com.healthcare.appointmentsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "participant1_id", nullable = false)
    private Long participant1Id;

    @Column(name = "participant2_id", nullable = false)
    private Long participant2Id;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @PrePersist
    protected void onCreate() {
        if (lastMessageAt == null) lastMessageAt = LocalDateTime.now();
    }
}
