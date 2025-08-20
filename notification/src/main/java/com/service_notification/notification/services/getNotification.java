package com.service_notification.notification.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.service_notification.notification.repositories.NotificationRepository;

import com.service_notification.notification.models.Notification;

@Service
public class getNotification {

    private NotificationRepository notificationRepository;

    public List<Notification> getNotificationByUserId(String userId){
        return notificationRepository.findByUserIdAndReadFalse(userId);
    }
}
