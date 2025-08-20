package com.service_user.user.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service_user.user.models.User;
import com.service_user.user.repositories.UserRepository;


@Service
public class KafkaConsumerService {
    @Autowired
    private UserRepository userRepository;

    @KafkaListener(topics = "user.created", groupId = "user-service-group")
    public void consumeUserCreatedEvent(String message) {
        System.out.println("NOUVEL UTILISATEUR CRÉÉ : " + message);
        // ici tu peux parser le JSON et créer l'utilisateur dans ta base, ou autre
        // traitement
        try {
            User user = new ObjectMapper().readValue(message, User.class);
            userRepository.save(user);
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement de l'événement utilisateur créé : " + e.getMessage());
        }
    }
}