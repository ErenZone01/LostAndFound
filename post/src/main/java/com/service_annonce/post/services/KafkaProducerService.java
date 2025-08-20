package com.service_annonce.post.services;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Method to send a message to a Kafka topic for post creation
    public void sendPostCreationMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    // Method to send a message to a Kafka topic for post deletion
    public void sendPostDeletionMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
    // Method to send a message to a Kafka topic for post update
    public void sendPostUpdateMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
