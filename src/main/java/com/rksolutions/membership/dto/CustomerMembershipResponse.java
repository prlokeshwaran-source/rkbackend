package com.rksolutions.membership.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMembershipResponse {

    private Long id;
    private Long customerId;
    private String customerName;
    private Long membershipPlanId;
    private String membershipPlanName;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
