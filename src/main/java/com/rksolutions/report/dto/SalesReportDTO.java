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
public class SalesReportDTO {
    private BigDecimal totalSales;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private List<SalesReportItem> orders;
    private LocalDate fromDate;
    private LocalDate toDate;
}
