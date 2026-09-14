package com.rksolutions.wallet.entity;

import com.rksolutions.common.entity.BaseEntity;
import com.rksolutions.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "wallets")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Wallet extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(precision = 14, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "total_membership_earnings", precision = 14, scale = 2)
    private BigDecimal totalMembershipEarnings = BigDecimal.ZERO;

    @Column(name = "total_handbook_earnings", precision = 14, scale = 2)
    private BigDecimal totalHandbookEarnings = BigDecimal.ZERO;

    @Column(name = "total_bonus", precision = 14, scale = 2)
    private BigDecimal totalBonus = BigDecimal.ZERO;
}
