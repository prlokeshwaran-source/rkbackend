package com.rksolutions.settings.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralSettingsRequest {
    private String companyName;
    private String currencyCode;
    private Boolean maintenanceMode;
    private Boolean otpEnabled;
}
