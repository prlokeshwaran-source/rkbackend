package com.rksolutions.notification.mapper;

import com.rksolutions.notification.dto.NotificationResponse;
import com.rksolutions.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        if (notification == null) return null;

        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());

        if (notification.getUser() != null) {
            response.setUserId(notification.getUser().getId());
        }

        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setType(notification.getType());
        response.setRead(notification.getIsRead());
        response.setReferenceType(notification.getReferenceType());
        response.setReferenceId(notification.getReferenceId());
        response.setBroadcast(notification.getIsBroadcast());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }
}
