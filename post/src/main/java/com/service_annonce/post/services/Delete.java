package com.service_annonce.post.services;

import org.bson.types.ObjectId;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import com.service_annonce.post.Repositories.PostRepositories;

@Service
public class Delete {

    private final GridFsTemplate gridFsTemplate;

    private final PostRepositories postRepositories;

    private KafkaProducerService kafkaTemplate;

    public Delete(PostRepositories postRepositories, GridFsTemplate gridFsTemplate, KafkaProducerService kafkaTemplate) {
        this.postRepositories = postRepositories;
        this.gridFsTemplate = gridFsTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    public String deletePost(String postId) {
        if (!postRepositories.existsByPostId(postId)) {
            return "Post not found";
        }
        try {
            kafkaTemplate.sendPostDeletionMessage("post.deleted", postId);
            deleteImage(postRepositories.findByPostId(postId).getImageId());
            postRepositories.deleteByPostId(postId);
        } catch (Exception e) {
            return "Error deleting post: " + e.getMessage();
        }
        return "Post deleted successfully";
    }

    public void deleteImage(String id) {
        try {
            gridFsTemplate.delete(query(where("_id").is(new ObjectId(id))));
            System.out.println("Image supprimée avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la suppression de l'image : " + id);
        }
    }

}
