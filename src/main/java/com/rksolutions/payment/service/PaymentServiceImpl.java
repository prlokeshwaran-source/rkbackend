package com.rksolutions.payment.service;

import com.rksolutions.common.enums.PaymentStatus;
import com.rksolutions.common.exception.ResourceNotFoundException;
import com.rksolutions.order.entity.Order;
import com.rksolutions.order.repository.OrderRepository;
import com.rksolutions.payment.dto.PaymentRequest;
import com.rksolutions.payment.dto.PaymentResponse;
import com.rksolutions.payment.entity.Payment;
import com.rksolutions.payment.mapper.PaymentMapper;
import com.rksolutions.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));

        Payment payment = paymentMapper.toEntity(request);
        payment.setOrder(order);

        if (request.getTransactionId() != null && !request.getTransactionId().isBlank()) {
            payment.setTransactionId(request.getTransactionId());
        } else {
            payment.setTransactionId(generateTransactionId());
        }

        if (request.getPaymentStatus() == PaymentStatus.SUCCESS) {
            payment.setPaidAt(LocalDateTime.now());
        }

        Payment savedPayment = paymentRepository.save(payment);

        if (savedPayment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            order.setPaymentStatus(PaymentStatus.SUCCESS);
            orderRepository.save(order);
        }

        return paymentMapper.toResponse(savedPayment);
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<PaymentResponse> getAllPayments(PaymentStatus status) {
        if (status != null) {
            return paymentRepository.findAll().stream()
                    .filter(p -> p.getPaymentStatus() == status)
                    .map(paymentMapper::toResponse)
                    .collect(Collectors.toList());
        }
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Page<PaymentResponse> getAllPaymentsPaginated(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .map(paymentMapper::toResponse);
    }

    @Override
    @Transactional
    public PaymentResponse updatePaymentStatus(Long id, PaymentStatus status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        PaymentStatus oldStatus = payment.getPaymentStatus();
        payment.setPaymentStatus(status);

        if (status == PaymentStatus.SUCCESS && oldStatus != PaymentStatus.SUCCESS) {
            payment.setPaidAt(LocalDateTime.now());

            if (payment.getOrder() != null) {
                payment.getOrder().setPaymentStatus(PaymentStatus.SUCCESS);
                orderRepository.save(payment.getOrder());
            }
        }

        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponse(savedPayment);
    }
}
