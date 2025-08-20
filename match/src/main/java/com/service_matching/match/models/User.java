package com.service_matching.match.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(collection = "users") // Nom de la collection MongoDB
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class User {
    @Id
    private String id;
    @Field("userId")
    private String userId;
    @Field("name")
    @NotBlank(message = "Name is required", groups= ValidationGroups.Register.class)
    private String name;
    @Field("email")
    @NotBlank(message = "Email is required", groups= {ValidationGroups.Register.class, ValidationGroups.Login.class})
    @Email(message = "Email is invalid", groups= {ValidationGroups.Register.class, ValidationGroups.Login.class})
    private String email;
    @Field("password")
    @NotBlank(message = "Password is required", groups= {ValidationGroups.Register.class, ValidationGroups.Login.class})
    private String password;
    @Field("role")
    @NotBlank(message = "Role is required", groups= ValidationGroups.Register.class)
    private String role; //admin ou user
}