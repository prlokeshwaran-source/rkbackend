package com.rksolutions.notification.service;

import com.rksolutions.common.enums.NotificationType;
import com.rksolutions.notification.dto.NotificationRequest;
import com.rksolutions.notification.dto.NotificationResponse;
import com.rksolutions.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    Notification createNotification(Long userId, String title, String message,
                                    NotificationType type, String referenceType, Long referenceId);

    List<NotificationResponse> sendToUsers(NotificationRequest request);

    List<NotificationResponse> broadcast(String title, String message, NotificationType type);

    List<NotificationResponse> getUserNotifications(Long userId);

    Page<NotificationResponse> getUserNotificationsPaginated(Long userId, Pageable pageable);

    long getUnreadCount(Long userId);

    void markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);
}
