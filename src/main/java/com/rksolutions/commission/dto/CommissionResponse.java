package com.rksolutions.commission.dto;

import com.rksolutions.common.enums.CommissionStatus;
import com.rksolutions.common.enums.CommissionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommissionResponse {

    private Long id;
    private Long userId;
    private String userName;
    private Long customerId;
    private String customerName;
    private Long orderId;
    private Long paymentId;
    private CommissionType type;
    private BigDecimal amount;
    private CommissionStatus status;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private Long referenceId;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
