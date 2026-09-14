package com.rksolutions.order.dto;

import com.rksolutions.common.enums.OrderStatus;
import com.rksolutions.common.enums.PaymentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @Valid
    @Size(min = 1, message = "At least one order item is required")
    private List<OrderItemRequest> items;

    private String notes;

    private PaymentStatus paymentStatus;
}
