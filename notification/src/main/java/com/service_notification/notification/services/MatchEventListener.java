package com.service_notification.notification.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service_notification.notification.models.MatchEventDTO;
import com.service_notification.notification.models.Notification;
import com.service_notification.notification.repositories.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchEventListener {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate; // pour WebSocket

    @KafkaListener(topics = "match-events", groupId = "notification-service")
    public void listenMatchEvent(String event) {
        System.out.println("📥 [Kafka] Event brut reçu : " + event);

        MatchEventDTO match;
        try {
            match = new ObjectMapper().readValue(event, MatchEventDTO.class);
        } catch (Exception e) {
            System.out.println("❌ Erreur lors du parsing JSON : " + e.getMessage());
            return;
        }

        System.out.println("✅ Event désérialisé : " + match);
        System.out.println("👤 Destinataire : " + match.getOwnerId() + " | Expéditeur : " + match.getSenderId());

        Notification notification = Notification.builder()
                .receiverId(match.getOwnerId())
                .senderId(match.getSenderId())
                .message("Nouvelle correspondance pour votre post !")
                .read(false)
                .build();

        notificationRepository.save(notification);

        System.out.println("💾 Notification enregistrée en BD : " + notification);

        messagingTemplate.convertAndSendToUser(notification.getReceiverId(), "/notifications", notification);
        messagingTemplate.convertAndSendToUser(notification.getSenderId(), "/notifications", notification);

        System.out.println("📡 Notification envoyée sur WebSocket /notifications/" + match.getOwnerId());
    }

}
