package com.rksolutions.wallet.controller;

import com.rksolutions.wallet.dto.BalanceResponse;
import com.rksolutions.wallet.dto.WalletResponse;
import com.rksolutions.wallet.dto.WalletTransactionResponse;
import com.rksolutions.wallet.service.WalletService;
import com.rksolutions.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping
    public ResponseEntity<ApiResponse<WalletResponse>> getWallet(Authentication authentication) {
        com.rksolutions.auth.security.CustomUserDetails userDetails =
                (com.rksolutions.auth.security.CustomUserDetails) authentication.getPrincipal();
        WalletResponse response = walletService.getWalletByUserId(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Wallet retrieved successfully"));
    }

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<BalanceResponse>> getBalance(Authentication authentication) {
        com.rksolutions.auth.security.CustomUserDetails userDetails =
                (com.rksolutions.auth.security.CustomUserDetails) authentication.getPrincipal();
        BalanceResponse response = walletService.getBalance(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Balance retrieved successfully"));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<Page<WalletTransactionResponse>>> getTransactions(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        com.rksolutions.auth.security.CustomUserDetails userDetails =
                (com.rksolutions.auth.security.CustomUserDetails) authentication.getPrincipal();

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WalletTransactionResponse> transactions =
                walletService.getTransactions(userDetails.getId(), pageable);

        return ResponseEntity.ok(ApiResponse.success(transactions, "Transactions retrieved successfully"));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<WalletResponse>> getWalletByUserId(@PathVariable Long userId) {
        WalletResponse response = walletService.getWalletByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Wallet retrieved successfully"));
    }
}
