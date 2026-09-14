package com.rksolutions.approval.controller;

import com.rksolutions.approval.service.UserApprovalService;
import com.rksolutions.user.dto.UserResponse;
import com.rksolutions.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
public class UserApprovalController {

    @Autowired
    private UserApprovalService userApprovalService;

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getPendingUsers() {
        List<UserResponse> users = userApprovalService.getPendingUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "Pending users retrieved successfully"));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<UserResponse>> approveUser(@PathVariable Long id) {
        UserResponse response = userApprovalService.approveUser(id);
        return ResponseEntity.ok(ApiResponse.success(response, "User approved successfully"));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<UserResponse>> rejectUser(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        UserResponse response = userApprovalService.rejectUser(id, reason);
        return ResponseEntity.ok(ApiResponse.success(response, "User rejected successfully"));
    }
}
