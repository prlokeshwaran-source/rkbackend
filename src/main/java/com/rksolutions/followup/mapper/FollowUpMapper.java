package com.rksolutions.followup.mapper;

import com.rksolutions.followup.dto.FollowUpResponse;
import com.rksolutions.followup.entity.FollowUp;
import org.springframework.stereotype.Component;

@Component
public class FollowUpMapper {

    public FollowUpResponse toResponse(FollowUp followUp) {
        if (followUp == null) return null;

        FollowUpResponse response = new FollowUpResponse();
        response.setId(followUp.getId());

        if (followUp.getCustomer() != null) {
            response.setCustomerId(followUp.getCustomer().getId());
            response.setCustomerName(followUp.getCustomer().getName());
        }

        if (followUp.getAssignedTo() != null) {
            response.setAssignedToId(followUp.getAssignedTo().getId());
            response.setAssignedToName(followUp.getAssignedTo().getName());
        }

        if (followUp.getCreatedBy() != null) {
            response.setCreatedById(followUp.getCreatedBy().getId());
        }

        response.setScheduledAt(followUp.getScheduledAt());
        response.setCompletedAt(followUp.getCompletedAt());
        response.setStatus(followUp.getStatus());
        response.setRemarks(followUp.getRemarks());
        response.setCallType(followUp.getCallType());
        response.setCreatedAt(followUp.getCreatedAt());
        response.setUpdatedAt(followUp.getUpdatedAt());
        return response;
    }
}
