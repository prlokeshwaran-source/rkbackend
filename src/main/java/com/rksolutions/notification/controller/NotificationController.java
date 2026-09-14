package com.rksolutions.notification.controller;

import com.rksolutions.common.enums.NotificationType;
import com.rksolutions.common.response.ApiResponse;
import com.rksolutions.notification.dto.NotificationRequest;
import com.rksolutions.notification.dto.NotificationResponse;
import com.rksolutions.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUserNotifications(
            Authentication authentication) {
        com.rksolutions.auth.security.CustomUserDetails userDetails =
                (com.rksolutions.auth.security.CustomUserDetails) authentication.getPrincipal();
        List<NotificationResponse> notifications = notificationService.getUserNotifications(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(notifications, "Notifications retrieved successfully"));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(Authentication authentication) {
        com.rksolutions.auth.security.CustomUserDetails userDetails =
                (com.rksolutions.auth.security.CustomUserDetails) authentication.getPrincipal();
        long count = notificationService.getUnreadCount(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(count, "Unread count retrieved successfully"));
    }

    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotificationsPaginated(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        com.rksolutions.auth.security.CustomUserDetails userDetails =
                (com.rksolutions.auth.security.CustomUserDetails) authentication.getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<NotificationResponse> notifications =
                notificationService.getUserNotificationsPaginated(userDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(notifications, "Notifications retrieved successfully"));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id, null);
        return ResponseEntity.ok(ApiResponse.success(null, "Notification marked as read"));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(Authentication authentication) {
        com.rksolutions.auth.security.CustomUserDetails userDetails =
                (com.rksolutions.auth.security.CustomUserDetails) authentication.getPrincipal();
        notificationService.markAllAsRead(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "All notifications marked as read"));
    }

    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> broadcast(
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam NotificationType type) {
        List<NotificationResponse> notifications = notificationService.broadcast(title, message, type);
        return ResponseEntity.ok(ApiResponse.success(notifications, "Broadcast sent successfully"));
    }
}
