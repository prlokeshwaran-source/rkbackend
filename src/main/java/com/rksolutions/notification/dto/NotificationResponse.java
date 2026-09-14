package com.rksolutions.notification.dto;

import com.rksolutions.common.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private Long userId;
    private String title;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private String referenceType;
    private Long referenceId;
    private boolean isBroadcast;
    private LocalDateTime createdAt;
}
