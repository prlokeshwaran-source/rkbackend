package com.rksolutions.followup.controller;

import com.rksolutions.common.enums.FollowUpStatus;
import com.rksolutions.common.response.ApiResponse;
import com.rksolutions.followup.dto.FollowUpRequest;
import com.rksolutions.followup.dto.FollowUpResponse;
import com.rksolutions.followup.service.FollowUpService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/followups")
public class FollowUpController {

    @Autowired
    private FollowUpService followUpService;

    @PostMapping
    public ResponseEntity<ApiResponse<FollowUpResponse>> createFollowUp(
            @Valid @RequestBody FollowUpRequest request) {
        FollowUpResponse response = followUpService.createFollowUp(request);
        return ResponseEntity.ok(ApiResponse.created(response, "Follow-up created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FollowUpResponse>>> getAllFollowUps() {
        List<FollowUpResponse> followUps = followUpService.getAllFollowUps();
        return ResponseEntity.ok(ApiResponse.success(followUps, "Follow-ups retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FollowUpResponse>> getFollowUpById(@PathVariable Long id) {
        FollowUpResponse response = followUpService.getFollowUpById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Follow-up retrieved successfully"));
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<FollowUpResponse>>> getTodayFollowUps(
            @RequestParam Long userId) {
        List<FollowUpResponse> followUps = followUpService.getTodayFollowUps(userId);
        return ResponseEntity.ok(ApiResponse.success(followUps, "Today's follow-ups retrieved successfully"));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<FollowUpResponse>>> getUpcomingFollowUps(
            @RequestParam Long userId) {
        List<FollowUpResponse> followUps = followUpService.getUpcomingFollowUps(userId);
        return ResponseEntity.ok(ApiResponse.success(followUps, "Upcoming follow-ups retrieved successfully"));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<FollowUpResponse>> completeFollowUp(
            @PathVariable Long id,
            @RequestParam(required = false) String remarks) {
        FollowUpResponse response = followUpService.completeFollowUp(id, remarks);
        return ResponseEntity.ok(ApiResponse.success(response, "Follow-up completed successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFollowUp(@PathVariable Long id) {
        followUpService.deleteFollowUp(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Follow-up deleted successfully"));
    }
}
