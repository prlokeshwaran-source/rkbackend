package com.rksolutions.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommissionReportItem {
    private Long commissionId;
    private String userName;
    private String customerName;
    private Long orderId;
    private BigDecimal amount;
    private String status;
    private String type;
    private LocalDateTime createdAt;
}
