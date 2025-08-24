package com.service_notification.notification.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    @Field("receiverId")
    private String receiverId; // destinataire
    @Field("senderId")
    private String senderId; // celui qui a créé le post / match
    @Field("message")
    private String message;
    @Field("read")
    private boolean read;
}
