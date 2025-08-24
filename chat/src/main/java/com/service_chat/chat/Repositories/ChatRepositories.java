package com.service_chat.chat.Repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.service_chat.chat.models.ChatMessage;

public interface ChatRepositories extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByMatchIdAndPostId(String MatchId,
            String PostId);
}
