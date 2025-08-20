
package com.service_auth.auth.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service_auth.auth.config.JwtUtil;
import com.service_auth.auth.models.ApiResponse;
import com.service_auth.auth.models.User;
import com.service_auth.auth.repository.UserRepository;

@Service
public class AuthService {
    // Injection de dépendance
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public ApiResponse Register(User user) {
        PasswordEncoder pwd = new BCryptPasswordEncoder();
        if (userRepository.findUserByEmail(user.getEmail()).isPresent()) {
            return new ApiResponse(false, "Email deja utilisé");
        }
        user.setPassword(pwd.encode(user.getPassword()));
        if (!user.getRole().toLowerCase().equals("proprietaire")
                && !user.getRole().toLowerCase().equals("retrouveur")) {
            return new ApiResponse(false, "Role incorrect");
        }
        user.setUserId(UUID.randomUUID().toString());
        // Envoi de l'événement Kafka avant la création de l'utilisateur
        try {
            String userJson = new ObjectMapper().writeValueAsString(user);
            kafkaProducerService.sendUserCreatedEvent(userJson);
            userRepository.save(user);
        } catch (Exception e) {
            // Log l'erreur si besoin
            return new ApiResponse(false, "Erreur lors de la création de l'utilisateur");
        }

        return new ApiResponse(true, "Utilisateur crée");
    }

    public ApiResponse Login(User user) {
        Optional<User> userFound = userRepository.findUserByEmail(user.getEmail());
        System.out.println("user : " + user);
        if (userFound.isEmpty() || user.getEmail().isEmpty()) {
            System.out.println("erreur 1 ");
            return new ApiResponse(false, "Email ou mot de passe incorrect !");
        }

        PasswordEncoder pwd = new BCryptPasswordEncoder();
        if (pwd.matches(user.getPassword(), userFound.get().getPassword())) {
            // Générer le token JWT
            String token;
            try {
                token = JwtUtil.generateToken(userFound.get().getUserId(), userFound.get().getRole());
            } catch (Exception e) {
                e.printStackTrace();
                return new ApiResponse(false, "Erreur lors de la génération du token");
            }
            System.out.println("tout se passe bien token : " + token);
            return new ApiResponse(true, token);
        }
        System.out.println("erreur 2 ");
        return new ApiResponse(false, "Email ou mot de passe incorrect !");
    }
}
