package com.rksolutions.membership.mapper;

import com.rksolutions.membership.dto.CustomerMembershipResponse;
import com.rksolutions.membership.dto.MembershipPlanRequest;
import com.rksolutions.membership.dto.MembershipPlanResponse;
import com.rksolutions.membership.entity.CustomerMembership;
import com.rksolutions.membership.entity.MembershipPlan;
import org.springframework.stereotype.Component;

@Component
public class MembershipMapper {

    public MembershipPlanResponse toPlanResponse(MembershipPlan plan) {
        if (plan == null) return null;

        MembershipPlanResponse response = new MembershipPlanResponse();
        response.setId(plan.getId());
        response.setName(plan.getName());
        response.setPrice(plan.getPrice());
        response.setDuration(plan.getDuration());
        response.setCommissionAmount(plan.getCommissionAmount());
        response.setMaxCustomers(plan.getMaxCustomers());
        response.setActive(plan.getActive());
        response.setDescription(plan.getDescription());
        response.setCreatedAt(plan.getCreatedAt());
        response.setUpdatedAt(plan.getUpdatedAt());
        return response;
    }

    public MembershipPlan toPlanEntity(MembershipPlanRequest request) {
        if (request == null) return null;

        MembershipPlan plan = new MembershipPlan();
        plan.setName(request.getName());
        plan.setPrice(request.getPrice());
        plan.setDuration(request.getDuration());
        plan.setCommissionAmount(request.getCommissionAmount());
        plan.setMaxCustomers(request.getMaxCustomers());
        plan.setActive(request.getActive());
        plan.setDescription(request.getDescription());
        return plan;
    }

    public void updatePlanEntityFromRequest(MembershipPlanRequest request, MembershipPlan plan) {
        if (request.getName() != null) plan.setName(request.getName());
        if (request.getPrice() != null) plan.setPrice(request.getPrice());
        if (request.getDuration() != null) plan.setDuration(request.getDuration());
        if (request.getCommissionAmount() != null) plan.setCommissionAmount(request.getCommissionAmount());
        if (request.getMaxCustomers() != null) plan.setMaxCustomers(request.getMaxCustomers());
        if (request.getActive() != null) plan.setActive(request.getActive());
        if (request.getDescription() != null) plan.setDescription(request.getDescription());
    }

    public CustomerMembershipResponse toMembershipResponse(CustomerMembership membership) {
        if (membership == null) return null;

        CustomerMembershipResponse response = new CustomerMembershipResponse();
        response.setId(membership.getId());

        if (membership.getCustomer() != null) {
            response.setCustomerId(membership.getCustomer().getId());
            response.setCustomerName(membership.getCustomer().getName());
        }

        if (membership.getMembershipPlan() != null) {
            response.setMembershipPlanId(membership.getMembershipPlan().getId());
            response.setMembershipPlanName(membership.getMembershipPlan().getName());
        }

        response.setStartDate(membership.getStartDate());
        response.setExpiryDate(membership.getExpiryDate());
        response.setTotalAmount(membership.getTotalAmount());
        response.setDiscountAmount(membership.getDiscountAmount());
        response.setStatus(membership.getStatus().name());
        response.setCreatedAt(membership.getCreatedAt());
        response.setUpdatedAt(membership.getUpdatedAt());
        return response;
    }
}
