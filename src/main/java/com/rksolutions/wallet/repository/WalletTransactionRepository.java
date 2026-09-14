package com.rksolutions.wallet.repository;

import com.rksolutions.wallet.entity.Wallet;
import com.rksolutions.wallet.entity.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByWalletUserId(Long userId);

    Page<WalletTransaction> findByWalletUserId(Long userId, Pageable pageable);
}
