package com.rksolutions.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private Long totalMembers;
    private Long totalCustomers;
    private Long todayOrders;
    private BigDecimal todayRevenue;
    private Long pendingCalls;
    private BigDecimal pendingCommission;
    private BigDecimal totalRevenue;
    private BigDecimal totalCommissionPending;
    private BigDecimal totalWalletBalance;
    private Long pendingUsers;
    private Long totalOrders;
    private Long totalPayments;
    private Long totalCommissions;
}
