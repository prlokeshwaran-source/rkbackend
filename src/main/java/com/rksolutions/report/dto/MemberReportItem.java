package com.rksolutions.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberReportItem {
    private Long userId;
    private String userName;
    private String email;
    private String role;
    private String status;
    private Long directCustomers;
    private Long totalCommissions;
    private BigDecimal earnedCommission;
    private java.time.LocalDateTime joinedAt;
}
