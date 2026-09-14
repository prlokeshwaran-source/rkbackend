package com.rksolutions.notification.service;

import com.rksolutions.common.enums.NotificationType;
import com.rksolutions.common.exception.ResourceNotFoundException;
import com.rksolutions.notification.dto.NotificationRequest;
import com.rksolutions.notification.dto.NotificationResponse;
import com.rksolutions.notification.entity.Notification;
import com.rksolutions.notification.mapper.NotificationMapper;
import com.rksolutions.notification.repository.NotificationRepository;
import com.rksolutions.user.entity.User;
import com.rksolutions.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public Notification createNotification(Long userId, String title, String message,
                                           NotificationType type, String referenceType, Long referenceId) {
        Notification notification = new Notification();

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            notification.setUser(user);
        }

        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setReferenceType(referenceType);
        notification.setReferenceId(referenceId);
        notification.setIsRead(false);
        notification.setIsBroadcast(false);

        return notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> sendToUsers(NotificationRequest request) {
        List<Long> userIds = request.getUserIds();
        List<NotificationResponse> responses = new java.util.ArrayList<>();

        if (userIds != null) {
            for (Long userId : userIds) {
                Notification notification = createNotification(
                        userId,
                        request.getTitle(),
                        request.getMessage(),
                        request.getType(),
                        request.getReferenceType(),
                        request.getReferenceId()
                );
                responses.add(notificationMapper.toResponse(notification));
            }
        }

        return responses;
    }

    @Override
    public List<NotificationResponse> broadcast(String title, String message, NotificationType type) {
        List<User> allUsers = userRepository.findAll();
        List<NotificationResponse> responses = new java.util.ArrayList<>();

        for (User user : allUsers) {
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setType(type);
            notification.setIsRead(false);
            notification.setIsBroadcast(true);
            notificationRepository.save(notification);
            responses.add(notificationMapper.toResponse(notification));
        }

        return responses;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<NotificationResponse> getUserNotifications(Long userId) {
        return notificationRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Page<NotificationResponse> getUserNotificationsPaginated(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public long getUnreadCount(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId).size();
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        for (Notification notification : unread) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }
}
