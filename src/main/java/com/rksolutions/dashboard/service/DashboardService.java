package com.rksolutions.dashboard.service;

import com.rksolutions.dashboard.dto.DashboardResponse;

public interface DashboardService {

    DashboardResponse getUserDashboard(Long userId);

    DashboardResponse getAdminDashboard();

    DashboardResponse getManagerDashboard(Long userId);

    DashboardResponse getSuperAdminDashboard();
}
