package com.rksolutions.payment.mapper;

import com.rksolutions.payment.dto.PaymentRequest;
import com.rksolutions.payment.dto.PaymentResponse;
import com.rksolutions.payment.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        if (payment == null) return null;

        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());

        if (payment.getOrder() != null) {
            response.setOrderId(payment.getOrder().getId());
            response.setOrderNumber(payment.getOrder().getOrderNumber());
        }

        if (payment.getCustomer() != null) {
            response.setCustomerId(payment.getCustomer().getId());
            response.setCustomerName(payment.getCustomer().getName());
        }

        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setTransactionId(payment.getTransactionId());
        response.setPaymentStatus(payment.getPaymentStatus());
        response.setPaidAt(payment.getPaidAt());
        response.setNotes(payment.getNotes());
        response.setCollectedBy(payment.getCollectedBy());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());
        return response;
    }

    public Payment toEntity(PaymentRequest request) {
        if (request == null) return null;

        Payment payment = new Payment();
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionId(request.getTransactionId());
        payment.setPaymentStatus(request.getPaymentStatus() != null
                ? request.getPaymentStatus() : com.rksolutions.common.enums.PaymentStatus.PENDING);
        payment.setNotes(request.getNotes());
        payment.setCollectedBy(request.getCollectedById());

        if (request.getOrderId() != null) {
            payment.setOrder(new com.rksolutions.order.entity.Order());
            payment.getOrder().setId(request.getOrderId());
        }

        if (request.getCustomerId() != null) {
            payment.setCustomer(new com.rksolutions.customer.entity.Customer());
            payment.getCustomer().setId(request.getCustomerId());
        }

        return payment;
    }
}
