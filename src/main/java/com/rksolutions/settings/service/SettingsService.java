package com.rksolutions.settings.service;

import com.rksolutions.settings.dto.*;

public interface SettingsService {

    SettingsResponse getSettings();

    SettingsResponse updateGeneral(GeneralSettingsRequest request);

    SettingsResponse updateCommission(CommissionSettingsRequest request);

    SettingsResponse updatePayment(PaymentSettingsRequest request);

    SettingsResponse updateNotification(NotificationSettingsRequest request);
}
