package com.rksolutions.dashboard.controller;

import com.rksolutions.dashboard.dto.DashboardResponse;
import com.rksolutions.dashboard.service.DashboardService;
import com.rksolutions.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<DashboardResponse>> getUserDashboard(@RequestParam Long userId) {
        DashboardResponse response = dashboardService.getUserDashboard(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "User dashboard retrieved successfully"));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DashboardResponse>> getAdminDashboard() {
        DashboardResponse response = dashboardService.getAdminDashboard();
        return ResponseEntity.ok(ApiResponse.success(response, "Admin dashboard retrieved successfully"));
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<DashboardResponse>> getManagerDashboard(@RequestParam Long userId) {
        DashboardResponse response = dashboardService.getManagerDashboard(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Manager dashboard retrieved successfully"));
    }

    @GetMapping("/super-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DashboardResponse>> getSuperAdminDashboard() {
        DashboardResponse response = dashboardService.getSuperAdminDashboard();
        return ResponseEntity.ok(ApiResponse.success(response, "Super admin dashboard retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @RequestParam String role, @RequestParam(required = false) Long userId) {
        DashboardResponse response = switch (role) {
            case "ROLE_SUPER_ADMIN" -> dashboardService.getSuperAdminDashboard();
            case "ROLE_ADMIN" -> dashboardService.getAdminDashboard();
            case "ROLE_MANAGER" -> dashboardService.getManagerDashboard(userId);
            default -> dashboardService.getUserDashboard(userId);
        };

        return ResponseEntity.ok(ApiResponse.success(response, "Dashboard retrieved successfully"));
    }
}
