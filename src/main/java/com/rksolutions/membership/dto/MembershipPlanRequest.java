package com.rksolutions.membership.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPlanRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private BigDecimal price = BigDecimal.ZERO;

    @NotNull(message = "Duration is required")
    private Integer duration;

    private BigDecimal commissionAmount = BigDecimal.ZERO;

    private Integer maxCustomers;

    private Boolean active = true;

    private String description;
}
