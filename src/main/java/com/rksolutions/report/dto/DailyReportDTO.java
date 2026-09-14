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
public class DailyReportDTO {
    private LocalDate date;
    private BigDecimal todaySales;
    private Long todayOrders;
    private Long todayCustomers;
    private Long pendingFollowUps;
    private BigDecimal pendingCommission;
}
