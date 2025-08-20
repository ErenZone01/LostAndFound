package com.service_annonce.post.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.gridfs.GridFSBucket;
import com.service_annonce.post.Repositories.PostRepositories;
import com.service_annonce.post.Repositories.UserRepository;
import com.service_annonce.post.models.Category;
import com.service_annonce.post.models.Post;

@Service
public class Create {

    private final PostRepositories postRepositories;
    private final UserRepository userRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public Create(PostRepositories postRepositories, UserRepository userRepository,
            KafkaProducerService kafkaProducerService) {
        this.postRepositories = postRepositories;
        this.userRepository = userRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    // Logic for creating a post will go here
    public String createPost(String id_User, Post post, MultipartFile image) {
        // Vérifier que l'utilisateur existe
        if (!userRepository.findByUserId(id_User).isPresent()) {
            return "User not found";
        }

        // Validation de la catégorie
        Category category;
        try {
            category = Category.valueOf(post.getCategory().toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            return "Catégorie invalide";
        }

        // Enregistrer l'image dans GridFS si elle est présente
        if (image != null && !image.isEmpty()) {
            try {
                String imageId = storeFile(image);
                post.setImageId(imageId); // Assure-toi que Post a un champ imageId de type String
            } catch (IOException e) {
                return "Erreur lors de l'enregistrement de l'image";
            }
        }

        post.setUserId(id_User);
        post.setPostId(UUID.randomUUID().toString());
        post.setCategory(category);
        post.setActive(true); // Assure que le post est actif par défaut

        try {
            // Envoi du message Kafka pour la création du post
            // json serialization of Post object
            String postJson = new ObjectMapper().writeValueAsString(post); // Assuming Post has a proper toString method or a JSON serialization method
            kafkaProducerService.sendPostCreationMessage("post.created", postJson);
            postRepositories.save(post);
        } catch (Exception e) {
            return "Erreur lors de la création du post: " + e.getMessage();
        }


        return "Post created successfully";
    }

    // Enregistrer un fichier et retourner son id (ObjectId)
    public String storeFile(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            ObjectId id = gridFsTemplate.store(inputStream, file.getOriginalFilename(), file.getContentType());
            return id.toHexString();
        }
    }
}
