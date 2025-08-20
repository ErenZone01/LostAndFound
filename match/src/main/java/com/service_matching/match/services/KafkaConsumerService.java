package com.service_matching.match.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service_matching.match.Repositories.PostRepositories;
import com.service_matching.match.Repositories.UserRepository;
import com.service_matching.match.models.Post;
import com.service_matching.match.models.User;

@Service
public class KafkaConsumerService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepositories postRepositories;

    @Autowired
    private MatchService matchService;

    @KafkaListener(topics = "user.created", groupId = "match-service-group")
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

    @KafkaListener(topics = "user.updated", groupId = "match-service-group")
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

    @KafkaListener(topics = "user.deleted", groupId = "match-service-group")
    public void consumeUserDeletedEvent(String id) {
        try {
            userRepository.deleteByUserId(id); // ici tu peux parser le JSON et supprimer l'utilisateur de ta base, ou
                                               // autre
            System.out.println("UTILISATEUR SUPPRIMÉ : " + id);
            // traitement
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement de l'événement utilisateur supprimé : " + e.getMessage());
        }
    }

    @KafkaListener(topics = "post.created", groupId = "match-service-group")
    public void consumePostCreatedEvent(String message) {
        try {
            Post post = new ObjectMapper().readValue(message, Post.class);
            postRepositories.save(post);
            matchService.UpdateMatchWhenPostCreated(post);
            System.out.println("NOUVEAU POST CRÉÉ : " + post);
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement de l'événement post créé : " + e.getMessage());
        }
    }

    @KafkaListener(topics = "post.updated", groupId = "match-service-group")
    public void consumePostUpdatedEvent(String message) {
        try {
            Post post = new ObjectMapper().readValue(message, Post.class);
            Post existingPost = postRepositories.findByPostId(post.getPostId());
            // Update the post
            existingPost.setType(post.getType());
            existingPost.setLieu(post.getLieu());
            existingPost.setDate(post.getDate());
            existingPost.setDescription(post.getDescription());
            existingPost.setCategory(post.getCategory());
            postRepositories.save(existingPost);
            matchService.UpdateMatchWhenPostCreated(existingPost);
            System.out.println("POST MIS À JOUR : " + post);
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement de l'événement post mis à jour : " + e.getMessage());
        }
    }

    @KafkaListener(topics = "post.deleted", groupId = "match-service-group")
    public void consumePostDeletedEvent(String id) {
        try {
            postRepositories.deleteByPostId(id); // ici tu peux parser le JSON et supprimer le post de ta base, ou
            System.out.println("POST SUPPRIMÉ : " + id);
            // autre traitement
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement de l'événement post supprimé : " + e.getMessage());
        }
    }

}