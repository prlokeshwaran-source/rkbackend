package com.rksolutions.order.service;

import com.rksolutions.common.enums.OrderStatus;
import com.rksolutions.common.enums.PaymentStatus;
import com.rksolutions.order.dto.OrderRequest;
import com.rksolutions.order.dto.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);

    OrderResponse getOrderById(Long id);

    OrderResponse getOrderByNumber(String orderNumber);

    List<OrderResponse> getAllOrders(OrderStatus orderStatus, PaymentStatus paymentStatus);

    Page<OrderResponse> getAllOrdersPaginated(Pageable pageable);

    OrderResponse updateOrderStatus(Long id, OrderStatus status);

    OrderResponse updatePaymentStatus(Long id, PaymentStatus status);
}
