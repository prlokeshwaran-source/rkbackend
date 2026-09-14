package com.rksolutions.customer.entity;

import com.rksolutions.common.entity.BaseEntity;
import com.rksolutions.common.enums.CustomerStatus;
import com.rksolutions.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_status_history")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerStatusHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 20)
    private CustomerStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private CustomerStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "changed_at")
    private java.time.LocalDateTime changedAt = java.time.LocalDateTime.now();
}
