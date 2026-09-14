package com.rksolutions.payment.service;

import com.rksolutions.common.enums.PaymentStatus;
import com.rksolutions.payment.dto.PaymentRequest;
import com.rksolutions.payment.dto.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest request);

    PaymentResponse getPaymentById(Long id);

    List<PaymentResponse> getAllPayments(PaymentStatus status);

    Page<PaymentResponse> getAllPaymentsPaginated(Pageable pageable);

    PaymentResponse updatePaymentStatus(Long id, PaymentStatus status);
}
