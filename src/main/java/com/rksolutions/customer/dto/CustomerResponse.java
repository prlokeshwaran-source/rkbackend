package com.rksolutions.customer.dto;

import com.rksolutions.common.enums.CustomerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String city;
    private String interestedIn;
    private String remarks;
    private CustomerStatus status;
    private Long createdBy;
    private String createdByName;
    private Long assignedTo;
    private String assignedToName;
    private LocalDateTime joinedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
