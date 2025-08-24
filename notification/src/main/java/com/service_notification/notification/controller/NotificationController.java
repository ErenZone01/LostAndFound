package com.service_notification.notification.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.service_notification.notification.models.Notification;
import com.service_notification.notification.repositories.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;

    private final NotificationRepository notificationRepository;

    @GetMapping("/getNotification")
    public List<Notification> getNotifications(@RequestParam String receiverId) {
        return notificationRepository.findByReceiverIdAndReadFalse(receiverId);
    }

    @MessageMapping("/notification")
    public void processMessage(@Payload Notification notification,
            Authentication authentication) {
        String userId = authentication.getPrincipal().toString();
        System.out.println("j'ai recu une notification : " + notification);
        // Pourquoi: validateur simple côté serveur pour éviter l'usurpation
        if (userId != null && notification.getSenderId() != null && !userId.equals(notification.getSenderId())) {
            throw new IllegalArgumentException("sender mismatch with authenticated principal");
        }
        System.out.println("le receveur c'est : " + notification.getReceiverId());
        System.out.println("Principal côté serveur: " + authentication.getPrincipal());

        messagingTemplate.convertAndSendToUser(notification.getReceiverId(), "/notification", notification);
        notificationRepository.save(notification);
    }

}
