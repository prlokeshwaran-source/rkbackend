package com.rksolutions.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReportDTO {
    private BigDecimal totalSales;
    private BigDecimal totalCommissions;
    private Long totalOrders;
    private Long totalCustomers;
    private List<MemberPerformanceItem> topPerformers;
    private LocalDate fromDate;
    private LocalDate toDate;
}
