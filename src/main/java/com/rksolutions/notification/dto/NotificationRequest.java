package com.rksolutions.notification.dto;

import com.rksolutions.common.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    private NotificationType type = NotificationType.SYSTEM;

    private String referenceType;

    private Long referenceId;

    private List<Long> userIds;
}
