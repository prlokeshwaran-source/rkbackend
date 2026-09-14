package com.rksolutions.settings.controller;

import com.rksolutions.common.response.ApiResponse;
import com.rksolutions.settings.dto.CommissionSettingsRequest;
import com.rksolutions.settings.dto.GeneralSettingsRequest;
import com.rksolutions.settings.dto.NotificationSettingsRequest;
import com.rksolutions.settings.dto.PaymentSettingsRequest;
import com.rksolutions.settings.dto.SettingsResponse;
import com.rksolutions.settings.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/settings")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SettingsResponse>> getSettings() {
        SettingsResponse response = settingsService.getSettings();
        return ResponseEntity.ok(ApiResponse.success(response, "Settings retrieved successfully"));
    }

    @PutMapping("/general")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SettingsResponse>> updateGeneral(
            @RequestBody GeneralSettingsRequest request) {
        SettingsResponse response = settingsService.updateGeneral(request);
        return ResponseEntity.ok(ApiResponse.success(response, "General settings updated successfully"));
    }

    @PutMapping("/commission")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SettingsResponse>> updateCommission(
            @RequestBody CommissionSettingsRequest request) {
        SettingsResponse response = settingsService.updateCommission(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Commission settings updated successfully"));
    }

    @PutMapping("/payment")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SettingsResponse>> updatePayment(
            @RequestBody PaymentSettingsRequest request) {
        SettingsResponse response = settingsService.updatePayment(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment settings updated successfully"));
    }

    @PutMapping("/notification")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SettingsResponse>> updateNotification(
            @RequestBody NotificationSettingsRequest request) {
        SettingsResponse response = settingsService.updateNotification(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Notification settings updated successfully"));
    }
}
