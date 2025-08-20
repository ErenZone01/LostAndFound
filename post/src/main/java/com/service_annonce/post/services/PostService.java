package com.service_annonce.post.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.service_annonce.post.Repositories.PostRepositories;
import com.service_annonce.post.Repositories.UserRepository;
import com.service_annonce.post.models.Post;
import com.service_annonce.post.models.PostDTO;

@Service
public class PostService {

    @Autowired
    private PostRepositories postRepositories;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageService imageService;

    public List<PostDTO> getAllPostsByUserId(String userId) {
        // Vérifie si l'utilisateur existe
        if (userRepository.findByUserId(userId).isEmpty()) {
            return new ArrayList<>();
        }

        // Récupère les posts
        List<Post> posts = postRepositories.findByUserId(userId);
        List<PostDTO> newPost = new ArrayList<>();
        
        // Transforme en PostDTO avec image en base64
        posts.stream()
                .forEach((p) -> {
                    try {
                        String imageBase64 = imageService.getImageBase64(p.getImageId());
                        PostDTO tmp = new PostDTO(p.getPostId(), p.getUserId(), p.getType(), p.getLieu(), p.getDate(),p.getImageId(),imageBase64, p.getDescription(), p.getCategory().toString(), p.getActive());
                        newPost.add(tmp);
                    } catch (Exception e) {
                        System.out.println("error: " + e);
                    }
                });
        return newPost;
    }

    public PostDTO getPostById(String postId) {
        if (postRepositories.findByPostId(postId) == null) {
            return new PostDTO();
        }
        Post post = postRepositories.findByPostId(postId);
        try {
            String imageBase64 = imageService.getImageBase64(post.getImageId());
            return new PostDTO(post.getPostId(), post.getUserId(), post.getType(), post.getLieu(), post.getDate(),post.getImageId(),imageBase64, post.getDescription(), post.getCategory().toString(), post.getActive());
        } catch (Exception e) {
            return new PostDTO();
        }
    }

}
