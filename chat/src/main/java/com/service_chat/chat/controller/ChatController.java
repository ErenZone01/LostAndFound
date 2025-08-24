// File: src/main/java/com/service_chat/chat/controller/ChatController.java
package com.service_chat.chat.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.service_chat.chat.Repositories.ChatRepositories;
import com.service_chat.chat.models.ChatMessage;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatRepositories chatRepositories;

    @GetMapping("/messages")
    public List<ChatMessage> getConversation(@RequestParam String matchId, @RequestParam String postId) {
        return chatRepositories.findByMatchIdAndPostId(matchId, postId);
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage,
            Authentication authentication) {
        String userId = authentication.getPrincipal().toString();
        System.out.println("j'ai recu, le msg est : " + chatMessage);
        // Pourquoi: validateur simple côté serveur pour éviter l'usurpation
        if (userId != null && chatMessage.getSender() != null && !userId.equals(chatMessage.getSender())) {
            throw new IllegalArgumentException("sender mismatch with authenticated principal");
        }
        System.out.println("le receveur c'est : " + chatMessage.getReceiver());
        System.out.println("Principal côté serveur: " + authentication.getPrincipal());

        messagingTemplate.convertAndSendToUser(chatMessage.getReceiver(), "/chat", chatMessage);
        chatRepositories.save(chatMessage);
    }

    @MessageMapping("/ping")
    @SendToUser("/queue/messages")
    public String ping(Authentication auth) {
        return "pong for " + auth.getName();
    }

}