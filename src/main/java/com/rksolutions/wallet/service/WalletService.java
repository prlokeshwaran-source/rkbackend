package com.rksolutions.wallet.service;

import com.rksolutions.common.enums.ReferenceType;
import com.rksolutions.wallet.dto.BalanceResponse;
import com.rksolutions.wallet.dto.WalletResponse;
import com.rksolutions.wallet.dto.WalletTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface WalletService {

    void creditWallet(Long userId, BigDecimal amount, String source,
                      String referenceType, Long referenceId, String description);

    void debitWallet(Long userId, BigDecimal amount, String source,
                     String referenceType, Long referenceId, String description);

    WalletResponse getWalletByUserId(Long userId);

    BalanceResponse getBalance(Long userId);

    Page<WalletTransactionResponse> getTransactions(Long userId, Pageable pageable);
}
