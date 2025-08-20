package com.service_auth.auth.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service_auth.auth.models.User;
import com.service_auth.auth.repository.UserRepository;

@Service
public class KafkaConsumerService {
    @Autowired
    private UserRepository userRepository;

    @KafkaListener(topics = "user.updated", groupId = "auth-service-group")
    public void consumeUserUpdatedEvent(String userJson) {
        try {
            User user = new ObjectMapper().readValue(userJson, User.class);
            User existingUser = userRepository.findByUserId(user.getUserId()).get();
            // Update the user
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setRole(user.getRole());
            userRepository.save(existingUser);
            System.out.println("UTILISATEUR MIS À JOUR : " + userJson);

        } catch (Exception e) {
            System.err.println("Erreur lors du traitement de l'événement utilisateur créé : " + e.getMessage());
        }
    }
 
    @KafkaListener(topics = "user.deleted", groupId = "auth-service-group")
    public void consumeUserDeletedEvent(String id) {
        try {
            userRepository.deleteByUserId(id); // ici tu peux parser le JSON et supprimer l'utilisateur de ta base, ou autre
            System.out.println("UTILISATEUR SUPPRIMÉ : " + id);
            // traitement
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement de l'événement utilisateur supprimé : " + e.getMessage());
        }
    }

}
