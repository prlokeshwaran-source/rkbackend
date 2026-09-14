package com.rksolutions.wallet.mapper;

import com.rksolutions.wallet.dto.WalletTransactionResponse;
import com.rksolutions.wallet.entity.WalletTransaction;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    public WalletTransactionResponse toTransactionResponse(WalletTransaction transaction) {
        if (transaction == null) return null;

        WalletTransactionResponse response = new WalletTransactionResponse();
        response.setId(transaction.getId());
        response.setTransactionType(transaction.getTransactionType());
        response.setAmount(transaction.getAmount());
        response.setReferenceType(transaction.getReferenceType());
        response.setReferenceId(transaction.getReferenceId());
        response.setDescription(transaction.getDescription());
        response.setCreatedAt(transaction.getCreatedAt());
        return response;
    }
}
