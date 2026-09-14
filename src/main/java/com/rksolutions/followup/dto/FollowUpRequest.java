package com.rksolutions.followup.dto;

import com.rksolutions.common.enums.FollowUpStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowUpRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private Long assignedToId;

    @NotNull(message = "Scheduled time is required")
    private LocalDateTime scheduledAt;

    private FollowUpStatus status;

    private String remarks;

    private String callType;
}
