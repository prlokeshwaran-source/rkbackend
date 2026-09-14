package com.rksolutions.wallet.dto;

import com.rksolutions.common.enums.ReferenceType;
import com.rksolutions.common.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionResponse {

    private Long id;
    private TransactionType transactionType;
    private BigDecimal amount;
    private ReferenceType referenceType;
    private Long referenceId;
    private String description;
    private LocalDateTime createdAt;
}
