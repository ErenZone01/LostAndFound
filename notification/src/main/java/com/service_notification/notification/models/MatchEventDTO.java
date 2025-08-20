package com.service_notification.notification.models;

import lombok.Data;

@Data
public class MatchEventDTO {
    private String ownerId;
    private String senderId;
    private String postId;
}
