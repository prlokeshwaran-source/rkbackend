package com.rksolutions.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberPerformanceItem {
    private Long userId;
    private String userName;
    private Long ordersCount;
    private BigDecimal totalSales;
    private BigDecimal totalCommission;
}
