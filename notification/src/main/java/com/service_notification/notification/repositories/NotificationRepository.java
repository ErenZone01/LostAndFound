package com.service_notification.notification.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.service_notification.notification.models.Notification;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByReceiverIdAndReadFalse(String receiverId);
}
