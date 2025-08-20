package com.service_annonce.post.services;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Service
public class ImageService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    public String getImageBase64(String imageId) throws IOException {
        GridFSFile file = gridFsTemplate.findOne(
            new org.springframework.data.mongodb.core.query.Query(
                org.springframework.data.mongodb.core.query.Criteria.where("_id").is(imageId)
            )
        );

        GridFsResource resource = gridFsTemplate.getResource(file);

        byte[] bytes = resource.getInputStream().readAllBytes();

        // Transformer en Base64
        return "data:" + file.getMetadata().get("_contentType") + ";base64," 
                + Base64.getEncoder().encodeToString(bytes);
    }
}
