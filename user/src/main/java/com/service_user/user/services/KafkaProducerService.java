package com.service_user.user.services;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserUpdatedEvent(String userJson) {
        kafkaTemplate.send("user.updated", userJson);
    }
    public void sendUserDeletedEvent(String userId) {
        kafkaTemplate.send("user.deleted", userId);
    }
}
