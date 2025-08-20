package com.service_user.user.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service_user.user.models.User;
import com.service_user.user.models.UserDTO;
import com.service_user.user.repositories.UserRepository;

@Service
public class UserService {
    // Injecting UserRepository and KafkaProducerService
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO getProfile(String userId) {
        System.out.println(userId);
        if (!userRepository.findByUserId(userId).isPresent()) {
            return new UserDTO();
        }

        User user = userRepository.findByUserId(userId).get();
        System.out.println("----------------User : " + user);
        UserDTO newUser = new UserDTO(user.getUserId(), user.getName(), user.getEmail(), user.getRole());
        return newUser;
    }

    public UserDTO getUserById(String userId) {
        System.out.println("getUserid : "+ userId);
        if (!userRepository.findByUserId(userId).isPresent()){
            return new UserDTO();
        }
        User user = userRepository.findByUserId(userId).get();
        UserDTO newUser = new UserDTO(user.getUserId(), user.getName(), user.getEmail(), user.getRole());
        return newUser;
    }

    public String deleteUserById(String id) {
        // Check if the user exists
        if (!userRepository.findByUserId(id).isPresent()) {
            return "User not found";
        }
        try {
            userRepository.deleteByUserId(id);
            kafkaProducerService.sendUserDeletedEvent(id);
        } catch (Exception e) {
            return "Error while sending delete event";
        }
        return "User deleted successfully";
    }

    public String updateUser(String id, User user) {
        // Check if the user exists
        if (!userRepository.findByUserId(id).isPresent()) {
            return "User not found";
        }
        User existingUser = userRepository.findByUserId(id).get();
        // Update the user
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setRole(user.getRole());
        try {
            userRepository.save(existingUser);
            String update_user_json = new ObjectMapper().writeValueAsString(existingUser);
            kafkaProducerService.sendUserUpdatedEvent(update_user_json);
        } catch (Exception e) {
            return "Error while sending update event";
        }
        return "User updated successfully";
    }
}