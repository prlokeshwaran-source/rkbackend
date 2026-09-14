package com.rksolutions.followup.dto;

import com.rksolutions.common.enums.FollowUpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowUpResponse {

    private Long id;
    private Long customerId;
    private String customerName;
    private Long assignedToId;
    private String assignedToName;
    private Long createdById;
    private LocalDateTime scheduledAt;
    private LocalDateTime completedAt;
    private FollowUpStatus status;
    private String remarks;
    private String callType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
