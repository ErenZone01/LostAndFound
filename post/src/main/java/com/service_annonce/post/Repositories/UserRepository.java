package com.service_annonce.post.Repositories;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.service_annonce.post.models.User;


public interface UserRepository extends MongoRepository<User, String> {
    // Méthodes de recherche personnalisées
    Optional<User> findById(String id);
    Optional<User> findByUserId(String userId);
    Optional<User> findUserByEmail(String email);
    Optional<User> findByName(String name);
    List<User> findByRole(String role);
    List<User> findByNameContaining(String name);
    void deleteById(String id);
    void deleteByUserId(String userId);
}
