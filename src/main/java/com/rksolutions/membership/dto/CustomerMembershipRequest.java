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
public class CustomerMembershipRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Membership plan ID is required")
    private Long membershipPlanId;

    private LocalDateTime startDate;

    private Integer durationDays;

    private BigDecimal discountAmount = BigDecimal.ZERO;
}
