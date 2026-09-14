package com.rksolutions.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyReportDTO {
    private Integer year;
    private Integer month;
    private BigDecimal totalSales;
    private Long totalOrders;
    private Long totalCustomers;
    private BigDecimal totalCommissions;
    private Map<LocalDate, BigDecimal> dailySales;
}
