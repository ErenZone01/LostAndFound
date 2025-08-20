package com.service_annonce.post.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(collection = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Post {
    @Id
    private String id;
    @Field("post_id")
    private String postId;
    @Field("user_id")
    private String userId;
    @Field("type")
    @NotBlank(message = "Type cannot be blank")
    private String type;
    @Field("lieu")
    @NotBlank(message = "Lieu cannot be blank")
    private String lieu;
    @Field("date")
    @NotBlank(message = "Date cannot be blank")
    private String date;
    @Field("imageId")
    private String imageId;
    @Field("description")
    @NotNull(message = "Description cannot be null")
    @SingleWords(message = "Description must be a list of single words without spaces")
    private List<String> description;
    @Field("category")
    @NotNull(message = "Category cannot be blank")
    private Category category;
    @Field("active")
    private Boolean active;

    // exemple de post avec des valeurs dans mon commentaire :
    // {
    // "id": "1",
    // "postId": "post-1",
    // "userId": "user-1",
    // "type": "lost",
    // "lieu": "Paris",
    // "date": "2023-01-01",
    // "photo": "url_to_photo",
    // "description": "Description du post",
    // "category": "electronics"
    // }
}
