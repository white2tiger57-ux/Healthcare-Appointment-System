package com.healthcare.appointmentsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_preferences")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "email_notifications")
    @Builder.Default
    private Boolean emailNotifications = true;

    @Column(name = "sms_notifications")
    @Builder.Default
    private Boolean smsNotifications = false;

    @Column(name = "app_notifications")
    @Builder.Default
    private Boolean appNotifications = true;

    @Column(name = "reminder_time")
    @Builder.Default
    private String reminderTime = "24h";
}
