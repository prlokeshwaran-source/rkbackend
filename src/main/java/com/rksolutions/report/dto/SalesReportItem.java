package com.rksolutions.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesReportItem {
    private Long orderId;
    private String orderNumber;
    private String customerName;
    private BigDecimal amount;
    private LocalDateTime date;
    private String paymentStatus;
}
