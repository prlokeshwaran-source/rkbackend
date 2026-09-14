package com.rksolutions.commission.mapper;

import com.rksolutions.commission.dto.CommissionResponse;
import com.rksolutions.commission.entity.Commission;
import org.springframework.stereotype.Component;

@Component
public class CommissionMapper {

    public CommissionResponse toResponse(Commission commission) {
        if (commission == null) return null;

        CommissionResponse response = new CommissionResponse();
        response.setId(commission.getId());

        if (commission.getUser() != null) {
            response.setUserId(commission.getUser().getId());
            response.setUserName(commission.getUser().getName());
        }

        if (commission.getCustomer() != null) {
            response.setCustomerId(commission.getCustomer().getId());
            response.setCustomerName(commission.getCustomer().getName());
        }

        if (commission.getOrder() != null) {
            response.setOrderId(commission.getOrder().getId());
        }

        if (commission.getPayment() != null) {
            response.setPaymentId(commission.getPayment().getId());
        }

        response.setType(commission.getType());
        response.setAmount(commission.getAmount());
        response.setStatus(commission.getStatus());
        response.setReferenceId(commission.getReferenceId());
        response.setRemarks(commission.getRemarks());

        if (commission.getApprovedBy() != null) {
            response.setApprovedById(commission.getApprovedBy().getId());
            response.setApprovedByName(commission.getApprovedBy().getName());
        }

        response.setApprovedAt(commission.getApprovedAt());
        response.setCreatedAt(commission.getCreatedAt());
        response.setUpdatedAt(commission.getUpdatedAt());
        return response;
    }
}
