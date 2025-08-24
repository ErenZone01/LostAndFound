package com.service_chat.chat.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.service_chat.chat.models.ChatMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class chatService {

    private SimpMessagingTemplate messagingTemplate;

    public chatService(SimpMessagingTemplate messagingTemplate){
        this.messagingTemplate = messagingTemplate;
    }

    public void sendMessage(String userId, ChatMessage chat) {
        log.info("Sending WS CHAT to {} with payload {}", userId, chat);
        messagingTemplate.convertAndSendToUser(userId, "/chat", chat);
    }

}
