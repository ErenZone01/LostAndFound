package com.service_matching.match.services;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.bson.types.ObjectId;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.service_matching.match.Repositories.PostRepositories;
import com.service_matching.match.models.Post;
import com.service_matching.match.models.PostDTO;
@Service
public class GetAllPosts {
    private final GridFsTemplate gridFsTemplate;
    private final PostRepositories postRepositories;

    public GetAllPosts(GridFsTemplate gridFsTemplate, PostRepositories postRepositories) {
        this.gridFsTemplate = gridFsTemplate;
        this.postRepositories = postRepositories;
    }

    private String getFileAsBase64(String id) {
        try {
            GridFSFile file = gridFsTemplate.findOne(
                    query(where("_id").is(new ObjectId(id))));
            if (file != null) {
                try (var inputStream = gridFsTemplate.getResource(file).getInputStream()) {
                    byte[] bytes = inputStream.readAllBytes();
                    return Base64.getEncoder().encodeToString(bytes);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // pas d'image
    }

    public List<PostDTO> getAllPosts() {
        List<PostDTO> postDTOs = new ArrayList<>();
        List<Post> posts = postRepositories.findAll();
        for (Post post : posts) {
            if (post.getImageId() != null) {
                PostDTO postDTO = new PostDTO();
                postDTO.setPostId(post.getPostId());
                postDTO.setUserId(post.getUserId());
                postDTO.setType(post.getType());
                postDTO.setLieu(post.getLieu());
                postDTO.setDate(post.getDate());
                postDTO.setImageId(post.getImageId());
                postDTO.setDescription(post.getDescription());
                postDTO.setCategory(post.getCategory());
                postDTO.setImageBase64(getFileAsBase64(post.getImageId()));
                postDTOs.add(postDTO);
            }
        }
        return postDTOs;
    }
}
