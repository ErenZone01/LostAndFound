package com.service_annonce.post.Repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.service_annonce.post.models.Category;
import com.service_annonce.post.models.Post;

public interface PostRepositories extends MongoRepository<Post, String> {
    // Custom query methods can be defined here if needed
    List<Post> findByUserId(String userId);
    List<Post> findByCategory(String category);
    List<Post> findByType(String type);
    List<Post> findByLieu(String lieu);
    List<Post> findByDate(String date);
    List<Post> findByCategoryAndDateAndLieu(Category category, String date, String lieu);
    Post findByPostId(String postId);
    void deleteByPostId(String postId);
    Boolean existsByPostId(String postId);
}
