package com.rksolutions.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommissionReportDTO {
    private BigDecimal totalCommissions;
    private Long totalCommissionsCount;
    private BigDecimal pendingAmount;
    private BigDecimal approvedAmount;
    private BigDecimal paidAmount;
    private List<CommissionReportItem> commissions;
}
