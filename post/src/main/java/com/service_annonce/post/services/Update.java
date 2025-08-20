package com.service_annonce.post.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.service_annonce.post.Repositories.PostRepositories;
import com.service_annonce.post.models.Category;
import com.service_annonce.post.models.Post;

@Service
public class Update {
    private final PostRepositories postRepositories;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private Delete deleteService;

    @Autowired
    private KafkaProducerService kafkaTemplate;

    public Update(PostRepositories postRepositories, Delete deleteService, KafkaProducerService kafkaTemplate) {
        this.postRepositories = postRepositories;
        this.deleteService = deleteService;
        this.kafkaTemplate = kafkaTemplate;
    }

    // Logic for updating a post will go here
    public String updatePost(String id_Post, Post post, MultipartFile image) {
        if (!postRepositories.existsByPostId(id_Post)) {
            return "Post not found";
        }

        // Conversion sécurisée du String vers l'enum Category
        Category category;
        try {
            category = Category.valueOf(post.getCategory().toString().toLowerCase());
        } catch (IllegalArgumentException e) {
            return "Catégorie invalide";
        }
        try {
            Post existingPost = postRepositories.findByPostId(id_Post);
            existingPost.setType(post.getType());
            existingPost.setLieu(post.getLieu());
            existingPost.setDate(post.getDate());
            existingPost.setImageId(post.getImageId());
            existingPost.setDescription(post.getDescription());
            existingPost.setCategory(category);
            
            // Envoi d'un message Kafka pour notifier la mise à jour du post
            String postJson = new ObjectMapper().writeValueAsString(existingPost);// Assuming Post has a proper toString method or a JSON serialization method
            kafkaTemplate.sendPostUpdateMessage("post.updated", postJson);

            //supprimer l'ancienne image si elle existe
            if (existingPost.getImageId() != null) {
                deleteService.deleteImage(existingPost.getImageId());
            }

            // Enregistrer l'image dans GridFS si elle est présente
            if (image != null && !image.isEmpty()) {
                String imageId = storeFile(image);
                existingPost.setImageId(imageId); // Assure-toi que Post a un champ imageId de type String
            }
            postRepositories.save(existingPost);
            return "Post updated successfully";
        } catch (Exception e) {
            return "Erreur lors de la mise à jour du post: " + e.getMessage();
        }
    }

    // Enregistrer un fichier et retourner son id (ObjectId)
    public String storeFile(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            ObjectId id = gridFsTemplate.store(inputStream, file.getOriginalFilename(), file.getContentType());
            return id.toHexString();
        }
    }

    // Récupérer un fichier via son id
    public Optional<GridFSFile> getFile(String id) {
        return Optional.ofNullable(gridFsTemplate.findOne(
                query(where("_id").is(new ObjectId(id)))));
    }
}
