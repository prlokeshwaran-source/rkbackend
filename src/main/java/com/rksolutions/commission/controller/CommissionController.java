package com.rksolutions.commission.controller;

import com.rksolutions.common.enums.CommissionStatus;
import com.rksolutions.commission.dto.CommissionResponse;
import com.rksolutions.commission.dto.CommissionStatusRequest;
import com.rksolutions.commission.service.CommissionService;
import com.rksolutions.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/commissions")
public class CommissionController {

    @Autowired
    private CommissionService commissionService;

    @PostMapping("/calculate/{paymentId}")
    @PreAuthorize("hasRole('SYSTEM') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<CommissionResponse>>> calculateCommission(
            @PathVariable Long paymentId) {
        List<CommissionResponse> commissions = commissionService.calculateCommissionFromPayment(paymentId);
        return ResponseEntity.ok(ApiResponse.success(commissions, "Commission calculated successfully"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<CommissionResponse>>> getAllCommissions(
            @RequestParam(required = false) CommissionStatus status) {
        List<CommissionResponse> commissions;
        if (status != null) {
            commissions = commissionService.getAllCommissions().stream()
                    .filter(c -> c.getStatus() == status)
                    .collect(java.util.stream.Collectors.toList());
        } else {
            commissions = commissionService.getAllCommissions();
        }
        return ResponseEntity.ok(ApiResponse.success(commissions, "Commissions retrieved successfully"));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<CommissionResponse>>> getPendingCommissions() {
        List<CommissionResponse> commissions = commissionService.getPendingCommissions();
        return ResponseEntity.ok(ApiResponse.success(commissions, "Pending commissions retrieved successfully"));
    }

    @GetMapping("/my-commissions")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<CommissionResponse>>> getMyCommissions(
            Authentication authentication) {
        com.rksolutions.auth.security.CustomUserDetails userDetails =
                (com.rksolutions.auth.security.CustomUserDetails) authentication.getPrincipal();
        List<CommissionResponse> commissions = commissionService.getCommissionsByUser(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(commissions, "Your commissions retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CommissionResponse>> getCommissionById(@PathVariable Long id) {
        CommissionResponse response = commissionService.getCommissionById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Commission retrieved successfully"));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CommissionResponse>> approveCommission(
            @PathVariable Long id,
            Authentication authentication) {
        com.rksolutions.auth.security.CustomUserDetails userDetails =
                (com.rksolutions.auth.security.CustomUserDetails) authentication.getPrincipal();
        CommissionResponse response = commissionService.approveCommission(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Commission approved successfully"));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CommissionResponse>> rejectCommission(
            @PathVariable Long id,
            @Valid @RequestBody CommissionStatusRequest request,
            Authentication authentication) {
        com.rksolutions.auth.security.CustomUserDetails userDetails =
                (com.rksolutions.auth.security.CustomUserDetails) authentication.getPrincipal();
        CommissionResponse response = commissionService.rejectCommission(id, request, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Commission rejected successfully"));
    }
}
