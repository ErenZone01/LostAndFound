package com.service_matching.match.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PostDTO {
    private String postId;
    private String userId;
    private String type;
    private String lieu;
    private String date;
    private String imageId;
    private String imageBase64; // Pour l'image en base64
    private List<String> description;
    private Category category;

    // Getters and Setters
}
