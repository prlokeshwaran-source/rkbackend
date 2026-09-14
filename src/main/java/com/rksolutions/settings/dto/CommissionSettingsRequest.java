package com.rksolutions.settings.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommissionSettingsRequest {
    private Boolean commissionEnabled;
    private BigDecimal defaultCommissionRate;
}
