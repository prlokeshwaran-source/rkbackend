package com.rksolutions.membership.controller;

import com.rksolutions.membership.dto.MembershipPlanRequest;
import com.rksolutions.membership.dto.MembershipPlanResponse;
import com.rksolutions.membership.dto.CustomerMembershipRequest;
import com.rksolutions.membership.dto.CustomerMembershipResponse;
import com.rksolutions.membership.service.MembershipService;
import com.rksolutions.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MembershipController {

    @Autowired
    private MembershipService membershipService;

    @GetMapping("/membership-plans")
    public ResponseEntity<ApiResponse<List<MembershipPlanResponse>>> getActivePlans() {
        List<MembershipPlanResponse> plans = membershipService.getActivePlans();
        return ResponseEntity.ok(ApiResponse.success(plans, "Membership plans retrieved successfully"));
    }

    @GetMapping("/membership-plans/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<MembershipPlanResponse>>> getAllPlans() {
        List<MembershipPlanResponse> plans = membershipService.getAllPlans();
        return ResponseEntity.ok(ApiResponse.success(plans, "All membership plans retrieved successfully"));
    }

    @GetMapping("/membership-plans/{id}")
    public ResponseEntity<ApiResponse<MembershipPlanResponse>> getPlanById(@PathVariable Long id) {
        MembershipPlanResponse response = membershipService.getPlanById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Membership plan retrieved successfully"));
    }

    @PostMapping("/admin/membership-plans")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<MembershipPlanResponse>> createPlan(
            @Valid @RequestBody MembershipPlanRequest request) {
        MembershipPlanResponse response = membershipService.createPlan(request);
        return ResponseEntity.ok(ApiResponse.created(response, "Membership plan created successfully"));
    }

    @PutMapping("/admin/membership-plans/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<MembershipPlanResponse>> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody MembershipPlanRequest request) {
        MembershipPlanResponse response = membershipService.updatePlan(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Membership plan updated successfully"));
    }

    @DeleteMapping("/admin/membership-plans/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePlan(@PathVariable Long id) {
        membershipService.deletePlan(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Membership plan deleted successfully"));
    }

    @PostMapping("/customer-memberships")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CustomerMembershipResponse>> assignMembership(
            @Valid @RequestBody CustomerMembershipRequest request) {
        CustomerMembershipResponse response = membershipService.assignMembership(request);
        return ResponseEntity.ok(ApiResponse.created(response, "Membership assigned successfully"));
    }

    @GetMapping("/customer-memberships/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('SUPER_ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<CustomerMembershipResponse>> getMembershipById(@PathVariable Long id) {
        CustomerMembershipResponse response = membershipService.getMembershipById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Customer membership retrieved successfully"));
    }

    @GetMapping("/customers/{customerId}/memberships")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('SUPER_ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<CustomerMembershipResponse>>> getMembershipsByCustomer(
            @PathVariable Long customerId) {
        List<CustomerMembershipResponse> memberships = membershipService.getMembershipsByCustomer(customerId);
        return ResponseEntity.ok(ApiResponse.success(memberships, "Memberships retrieved successfully"));
    }
}
