package com.rksolutions.settings.entity;

import com.rksolutions.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "app_settings")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class AppSettings extends BaseEntity {

    @Column(name = "company_name")
    private String companyName = "RK Solutions";

    @Column(name = "commission_enabled")
    private Boolean commissionEnabled = true;

    @Column(name = "default_commission_rate", precision = 5, scale = 2)
    private BigDecimal defaultCommissionRate = BigDecimal.ZERO;

    @Column(name = "currency_code", length = 5)
    private String currencyCode = "INR";

    @Column(name = "payment_settings")
    private String paymentSettings;

    @Column(name = "notification_settings")
    private String notificationSettings;

    @Column(name = "otp_enabled")
    private Boolean otpEnabled = true;

    @Column(name = "maintenance_mode")
    private Boolean maintenanceMode = false;
}
