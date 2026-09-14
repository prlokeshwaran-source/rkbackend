package com.rksolutions.payment.dto;

import com.rksolutions.common.enums.PaymentMethod;
import com.rksolutions.common.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private String orderNumber;
    private Long customerId;
    private String customerName;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String transactionId;
    private PaymentStatus paymentStatus;
    private LocalDateTime paidAt;
    private String notes;
    private Long collectedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
