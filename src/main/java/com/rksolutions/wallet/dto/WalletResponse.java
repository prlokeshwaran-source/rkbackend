package com.rksolutions.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {

    private Long id;
    private Long userId;
    private String userName;
    private BigDecimal balance;
    private BigDecimal totalMembershipEarnings;
    private BigDecimal totalHandbookEarnings;
    private BigDecimal totalBonus;
    private List<WalletTransactionResponse> recentTransactions;
}
