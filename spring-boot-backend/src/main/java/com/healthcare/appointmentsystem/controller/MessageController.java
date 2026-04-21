package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.entity.*;
import com.healthcare.appointmentsystem.exception.ResourceNotFoundException;
import com.healthcare.appointmentsystem.repository.*;
import com.healthcare.appointmentsystem.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    public MessageController(MessageRepository messageRepository, ConversationRepository conversationRepository,
                             UserRepository userRepository, DoctorRepository doctorRepository,
                             PatientRepository patientRepository, AppointmentRepository appointmentRepository) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/doctors-for-patient")
    public ResponseEntity<List<Map<String, Object>>> getDoctorsForPatient(@AuthenticationPrincipal CustomUserDetails user) {
        if (!"patient".equals(user.getUserType())) {
            return ResponseEntity.badRequest().build();
        }
        List<Doctor> doctors = appointmentRepository.findDistinctDoctorsByPatientId(user.getRelatedId());
        List<Map<String, Object>> result = doctors.stream()
            .map(d -> {
                User docUser = userRepository.findByRelatedIdAndUserType(d.getId(), "doctor").orElse(null);
                Long userId = docUser != null ? docUser.getId() : null;
                return Map.of("id", (Object) userId, "name", (Object) d.getName());
            })
            .filter(m -> m.get("id") != null)
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/conversations")
    public ResponseEntity<Map<String, Object>> getConversations(@AuthenticationPrincipal CustomUserDetails user) {
        var conversations = conversationRepository.findByUserId(user.getId());
        var result = conversations.stream().map(c -> {
            Long otherId = c.getParticipant1Id().equals(user.getId()) ? c.getParticipant2Id() : c.getParticipant1Id();
            User otherUser = userRepository.findById(otherId).orElse(null);
            String otherName = "Unknown";
            if (otherUser != null) {
                if ("doctor".equals(otherUser.getUserType())) {
                    otherName = doctorRepository.findById(otherUser.getRelatedId()).map(Doctor::getName).orElse("Unknown");
                } else {
                    otherName = patientRepository.findById(otherUser.getRelatedId()).map(Patient::getName).orElse("Unknown");
                }
            }
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("conversation_id", c.getId());
            m.put("other_user_id", otherId);
            m.put("other_user_name", otherName);
            m.put("other_user_type", otherUser != null ? otherUser.getUserType() : null);
            m.put("last_message_at", c.getLastMessageAt());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("success", true, "conversations", result));
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<Map<String, Object>> getMessages(
            @PathVariable Long conversationId, @AuthenticationPrincipal CustomUserDetails user) {
        Conversation c = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));
        Long otherId = c.getParticipant1Id().equals(user.getId()) ? c.getParticipant2Id() : c.getParticipant1Id();
        var messages = messageRepository.findConversationMessages(user.getId(), otherId);
        return ResponseEntity.ok(Map.of("success", true, "messages", messages));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal CustomUserDetails user) {
        Long receiverId = Long.valueOf(body.get("receiverId").toString());
        String content = (String) body.get("content");

        var conversation = conversationRepository.findByParticipants(user.getId(), receiverId)
                .orElseGet(() -> conversationRepository.save(
                        Conversation.builder().participant1Id(user.getId()).participant2Id(receiverId).build()));

        Message message = Message.builder()
                .senderId(user.getId()).receiverId(receiverId).content(content).build();
        message = messageRepository.save(message);

        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        return ResponseEntity.status(201).body(Map.of(
                "success", true, "messageId", message.getId(),
                "conversationId", conversation.getId()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> unreadCount(@AuthenticationPrincipal CustomUserDetails user) {
        long count = messageRepository.countByReceiverIdAndIsReadFalse(user.getId());
        return ResponseEntity.ok(Map.of("success", true, "unreadCount", count));
    }
}
