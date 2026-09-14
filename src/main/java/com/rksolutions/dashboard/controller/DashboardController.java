package com.rksolutions.dashboard.controller;

import com.rksolutions.dashboard.dto.DashboardResponse;
import com.rksolutions.dashboard.service.DashboardService;
import com.rksolutions.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    private Long getCurrentUserId(Authentication authentication) {
        com.rksolutions.auth.security.CustomUserDetails userDetails =
                (com.rksolutions.auth.security.CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<DashboardResponse>> getUserDashboard(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
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
    public ResponseEntity<ApiResponse<DashboardResponse>> getManagerDashboard(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
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
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(Authentication authentication) {
        com.rksolutions.auth.security.CustomUserDetails userDetails =
                (com.rksolutions.auth.security.CustomUserDetails) authentication.getPrincipal();

        String role = userDetails.getRoles().iterator().next().name();

        DashboardResponse response;
        if (role.equals("ROLE_SUPER_ADMIN")) {
            response = dashboardService.getSuperAdminDashboard();
        } else if (role.equals("ROLE_ADMIN")) {
            response = dashboardService.getAdminDashboard();
        } else if (role.equals("ROLE_MANAGER")) {
            response = dashboardService.getManagerDashboard(userDetails.getId());
        } else {
            response = dashboardService.getUserDashboard(userDetails.getId());
        }

        return ResponseEntity.ok(ApiResponse.success(response, "Dashboard retrieved successfully"));
    }
}
