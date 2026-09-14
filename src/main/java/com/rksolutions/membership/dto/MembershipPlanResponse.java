package com.rksolutions.membership.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPlanResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer duration;
    private BigDecimal commissionAmount;
    private Integer maxCustomers;
    private Boolean active;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
