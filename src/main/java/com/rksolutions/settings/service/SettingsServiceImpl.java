package com.rksolutions.settings.service;

import com.rksolutions.settings.dto.*;
import com.rksolutions.settings.dto.CommissionSettingsRequest;
import com.rksolutions.settings.dto.GeneralSettingsRequest;
import com.rksolutions.settings.dto.NotificationSettingsRequest;
import com.rksolutions.settings.dto.PaymentSettingsRequest;
import com.rksolutions.settings.dto.SettingsResponse;
import com.rksolutions.settings.entity.AppSettings;
import com.rksolutions.settings.repository.AppSettingsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SettingsServiceImpl implements SettingsService {

    @Autowired
    private AppSettingsRepository settingsRepository;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public SettingsResponse getSettings() {
        List<AppSettings> settings = settingsRepository.findAll();
        AppSettings appSettings = settings.isEmpty() ? new AppSettings() : settings.get(0);
        return toResponse(appSettings);
    }

    private AppSettings getOrCreateSettings() {
        List<AppSettings> settings = settingsRepository.findAll();
        if (settings.isEmpty()) {
            AppSettings newSettings = new AppSettings();
            return settingsRepository.save(newSettings);
        }
        return settings.get(0);
    }

    @Override
    public SettingsResponse updateGeneral(GeneralSettingsRequest request) {
        AppSettings settings = getOrCreateSettings();

        if (request.getCompanyName() != null) settings.setCompanyName(request.getCompanyName());
        if (request.getCurrencyCode() != null) settings.setCurrencyCode(request.getCurrencyCode());
        if (request.getMaintenanceMode() != null) settings.setMaintenanceMode(request.getMaintenanceMode());
        if (request.getOtpEnabled() != null) settings.setOtpEnabled(request.getOtpEnabled());

        AppSettings saved = settingsRepository.save(settings);
        return toResponse(saved);
    }

    @Override
    public SettingsResponse updateCommission(CommissionSettingsRequest request) {
        AppSettings settings = getOrCreateSettings();

        if (request.getCommissionEnabled() != null) settings.setCommissionEnabled(request.getCommissionEnabled());
        if (request.getDefaultCommissionRate() != null) settings.setDefaultCommissionRate(request.getDefaultCommissionRate());

        AppSettings saved = settingsRepository.save(settings);
        return toResponse(saved);
    }

    @Override
    public SettingsResponse updatePayment(PaymentSettingsRequest request) {
        AppSettings settings = getOrCreateSettings();

        if (request.getPaymentSettings() != null) settings.setPaymentSettings(request.getPaymentSettings());

        AppSettings saved = settingsRepository.save(settings);
        return toResponse(saved);
    }

    @Override
    public SettingsResponse updateNotification(NotificationSettingsRequest request) {
        AppSettings settings = getOrCreateSettings();

        if (request.getNotificationSettings() != null) settings.setNotificationSettings(request.getNotificationSettings());

        AppSettings saved = settingsRepository.save(settings);
        return toResponse(saved);
    }

    private SettingsResponse toResponse(AppSettings settings) {
        if (settings == null) return null;

        SettingsResponse response = new SettingsResponse();
        response.setId(settings.getId());
        response.setCompanyName(settings.getCompanyName());
        response.setCommissionEnabled(settings.getCommissionEnabled());
        response.setDefaultCommissionRate(settings.getDefaultCommissionRate() != null
                ? settings.getDefaultCommissionRate().toString() : "0");
        response.setCurrencyCode(settings.getCurrencyCode());
        response.setPaymentSettings(settings.getPaymentSettings());
        response.setNotificationSettings(settings.getNotificationSettings());
        response.setOtpEnabled(settings.getOtpEnabled());
        response.setMaintenanceMode(settings.getMaintenanceMode());
        response.setUpdatedAt(settings.getUpdatedAt() != null ? settings.getUpdatedAt().toString() : null);
        return response;
    }
}
