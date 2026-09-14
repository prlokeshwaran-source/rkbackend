package com.rksolutions.settings.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingsResponse {

    private Long id;
    private String companyName;
    private Boolean commissionEnabled;
    private String defaultCommissionRate;
    private String currencyCode;
    private String paymentSettings;
    private String notificationSettings;
    private Boolean otpEnabled;
    private Boolean maintenanceMode;
    private String updatedAt;
}
