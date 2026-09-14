package com.rksolutions.wallet.service;

import com.rksolutions.common.enums.ReferenceType;
import com.rksolutions.common.enums.TransactionType;
import com.rksolutions.common.exception.BadRequestException;
import com.rksolutions.common.exception.ResourceNotFoundException;
import com.rksolutions.wallet.dto.BalanceResponse;
import com.rksolutions.wallet.dto.WalletResponse;
import com.rksolutions.wallet.dto.WalletTransactionResponse;
import com.rksolutions.wallet.entity.Wallet;
import com.rksolutions.wallet.entity.WalletTransaction;
import com.rksolutions.wallet.mapper.WalletMapper;
import com.rksolutions.wallet.repository.WalletRepository;
import com.rksolutions.wallet.repository.WalletTransactionRepository;
import com.rksolutions.user.entity.User;
import com.rksolutions.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletTransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletMapper walletMapper;

    private Wallet findOrCreateWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                    Wallet wallet = new Wallet();
                    wallet.setUser(user);
                    wallet.setBalance(BigDecimal.ZERO);
                    wallet.setTotalMembershipEarnings(BigDecimal.ZERO);
                    wallet.setTotalHandbookEarnings(BigDecimal.ZERO);
                    wallet.setTotalBonus(BigDecimal.ZERO);
                    return walletRepository.save(wallet);
                });
    }

    @Override
    @Transactional
    public void creditWallet(Long userId, BigDecimal amount, String source,
                             String referenceType, Long referenceId, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Credit amount must be positive");
        }

        Wallet wallet = findOrCreateWallet(userId);

        wallet.setBalance(wallet.getBalance().add(amount));

        if ("membership".equalsIgnoreCase(source) || "MEMBERSHIP".equalsIgnoreCase(source)) {
            wallet.setTotalMembershipEarnings(wallet.getTotalMembershipEarnings().add(amount));
        } else if ("handbook".equalsIgnoreCase(source) || "HANDBOOK".equalsIgnoreCase(source)) {
            wallet.setTotalHandbookEarnings(wallet.getTotalHandbookEarnings().add(amount));
        } else if ("bonus".equalsIgnoreCase(source)) {
            wallet.setTotalBonus(wallet.getTotalBonus().add(amount));
        } else {
            wallet.setTotalMembershipEarnings(wallet.getTotalMembershipEarnings().add(amount));
        }

        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(wallet);
        transaction.setTransactionType(TransactionType.CREDIT);
        transaction.setAmount(amount);

        try {
            transaction.setReferenceType(ReferenceType.valueOf(referenceType.toUpperCase()));
        } catch (IllegalArgumentException e) {
            transaction.setReferenceType(null);
        }

        transaction.setReferenceId(referenceId);
        transaction.setDescription(description);
        transactionRepository.save(transaction);

        walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public void debitWallet(Long userId, BigDecimal amount, String source,
                            String referenceType, Long referenceId, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Debit amount must be positive");
        }

        Wallet wallet = findOrCreateWallet(userId);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient wallet balance. Current balance: " + wallet.getBalance());
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));

        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(wallet);
        transaction.setTransactionType(TransactionType.DEBIT);
        transaction.setAmount(amount);

        try {
            transaction.setReferenceType(ReferenceType.valueOf(referenceType.toUpperCase()));
        } catch (IllegalArgumentException e) {
            transaction.setReferenceType(null);
        }

        transaction.setReferenceId(referenceId);
        transaction.setDescription(description);
        transactionRepository.save(transaction);

        walletRepository.save(wallet);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public WalletResponse getWalletByUserId(Long userId) {
        Wallet wallet = findOrCreateWallet(userId);

        List<WalletTransactionResponse> recentTxns = transactionRepository
                .findByWalletUserId(userId).stream()
                .map(walletMapper::toTransactionResponse)
                .limit(10)
                .collect(Collectors.toList());

        WalletResponse response = new WalletResponse();
        response.setId(wallet.getId());
        response.setUserId(userId);
        response.setUserName(wallet.getUser().getName());
        response.setBalance(wallet.getBalance());
        response.setTotalMembershipEarnings(wallet.getTotalMembershipEarnings());
        response.setTotalHandbookEarnings(wallet.getTotalHandbookEarnings());
        response.setTotalBonus(wallet.getTotalBonus());
        response.setRecentTransactions(recentTxns);
        return response;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public BalanceResponse getBalance(Long userId) {
        Wallet wallet = findOrCreateWallet(userId);

        BalanceResponse response = new BalanceResponse();
        response.setBalance(wallet.getBalance());
        BigDecimal totalEarnings = wallet.getTotalMembershipEarnings()
                .add(wallet.getTotalHandbookEarnings())
                .add(wallet.getTotalBonus());
        response.setTotalEarnings(totalEarnings);
        return response;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Page<WalletTransactionResponse> getTransactions(Long userId, Pageable pageable) {
        walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));

        return transactionRepository.findByWalletUserId(userId, pageable)
                .map(walletMapper::toTransactionResponse);
    }
}
