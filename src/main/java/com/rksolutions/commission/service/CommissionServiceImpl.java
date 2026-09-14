package com.rksolutions.commission.service;

import com.rksolutions.commission.dto.CommissionResponse;
import com.rksolutions.commission.dto.CommissionStatusRequest;
import com.rksolutions.commission.entity.Commission;
import com.rksolutions.commission.mapper.CommissionMapper;
import com.rksolutions.commission.repository.CommissionRepository;
import com.rksolutions.common.enums.CommissionStatus;
import com.rksolutions.common.enums.CommissionType;
import com.rksolutions.common.exception.ResourceNotFoundException;
import com.rksolutions.customer.entity.Customer;
import com.rksolutions.handbook.entity.Handbook;
import com.rksolutions.handbook.repository.HandbookRepository;
import com.rksolutions.membership.entity.MembershipPlan;
import com.rksolutions.membership.repository.MembershipPlanRepository;
import com.rksolutions.notification.service.NotificationService;
import com.rksolutions.order.entity.Order;
import com.rksolutions.order.entity.OrderItem;
import com.rksolutions.order.repository.OrderRepository;
import com.rksolutions.payment.entity.Payment;
import com.rksolutions.payment.repository.PaymentRepository;
import com.rksolutions.user.entity.User;
import com.rksolutions.user.repository.UserRepository;
import com.rksolutions.wallet.service.WalletService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommissionServiceImpl implements CommissionService {

    @Autowired
    private CommissionRepository commissionRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HandbookRepository handbookRepository;

    @Autowired
    private MembershipPlanRepository membershipPlanRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CommissionMapper commissionMapper;

    @Override
    @Transactional
    public List<CommissionResponse> calculateCommissionFromPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        Order order = payment.getOrder();
        if (order == null) {
            throw new ResourceNotFoundException("Order not found for payment: " + paymentId);
        }

        User partner = order.getCreatedBy();
        Customer customer = order.getCustomer();

        List<Commission> commissions = new ArrayList<>();

        for (OrderItem item : order.getItems()) {
            BigDecimal commissionAmount = BigDecimal.ZERO;
            CommissionType type = CommissionType.MEMBERSHIP;

            if (item.getItemType() == com.rksolutions.common.enums.ItemType.HANDBOOK) {
                Handbook handbook = handbookRepository.findById(item.getItemId())
                        .orElse(null);
                if (handbook != null) {
                    commissionAmount = handbook.getCommissionAmount()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                    type = CommissionType.HANDBOOK;
                }
            } else if (item.getItemType() == com.rksolutions.common.enums.ItemType.MEMBERSHIP) {
                MembershipPlan plan = membershipPlanRepository.findById(item.getItemId())
                        .orElse(null);
                if (plan != null) {
                    commissionAmount = plan.getCommissionAmount();
                    type = CommissionType.MEMBERSHIP;
                }
            } else {
                continue;
            }

            if (commissionAmount.compareTo(BigDecimal.ZERO) > 0) {
                Commission commission = new Commission();
                commission.setUser(partner);
                commission.setCustomer(customer);
                commission.setOrder(order);
                commission.setPayment(payment);
                commission.setType(type);
                commission.setAmount(commissionAmount);
                commission.setStatus(CommissionStatus.PENDING);
                commission.setReferenceId(item.getItemId());
                commission.setRemarks("Commission for " + item.getItemName());
                commissions.add(commission);
            }
        }

        List<Commission> savedCommissions = commissionRepository.saveAll(commissions);

        // Notify partner about pending commission
        notificationService.createNotification(
                partner.getId(),
                "Commission Pending",
                "Commission of amount " + savedCommissions.stream()
                        .map(c -> c.getAmount().toString())
                        .collect(Collectors.joining(", ")) + " is pending approval",
                com.rksolutions.common.enums.NotificationType.COMMISSION,
                "COMMISSION",
                payment.getId()
        );

        return savedCommissions.stream()
                .map(commissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public CommissionResponse getCommissionById(Long id) {
        Commission commission = commissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found with id: " + id));
        return commissionMapper.toResponse(commission);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<CommissionResponse> getAllCommissions() {
        return commissionRepository.findAll().stream()
                .map(commissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<CommissionResponse> getPendingCommissions() {
        return commissionRepository.findByStatus(CommissionStatus.PENDING).stream()
                .map(commissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<CommissionResponse> getCommissionsByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return commissionRepository.findByUserId(userId).stream()
                .map(commissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Page<CommissionResponse> getAllCommissionsPaginated(Pageable pageable) {
        return commissionRepository.findAll(pageable)
                .map(commissionMapper::toResponse);
    }

    @Override
    @Transactional
    public CommissionResponse approveCommission(Long id, Long approvedById) {
        Commission commission = commissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found with id: " + id));

        if (commission.getStatus() != CommissionStatus.PENDING) {
            throw new IllegalStateException("Commission can only be approved from PENDING status");
        }

        User approver = userRepository.findById(approvedById)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found with id: " + approvedById));

        commission.setStatus(CommissionStatus.APPROVED);
        commission.setApprovedBy(approver);
        commission.setApprovedAt(LocalDateTime.now());

        Commission savedCommission = commissionRepository.save(commission);

        walletService.creditWallet(
                commission.getUser().getId(),
                commission.getAmount(),
                "commission",
                "COMMISSION",
                commission.getId(),
                "Commission approved for order #" + (commission.getOrder() != null
                        ? commission.getOrder().getOrderNumber() : "N/A")
        );

        notificationService.createNotification(
                commission.getUser().getId(),
                "Commission Approved",
                "Your commission of " + commission.getAmount() + " has been approved and credited to your wallet",
                com.rksolutions.common.enums.NotificationType.COMMISSION,
                "COMMISSION",
                commission.getId()
        );

        return commissionMapper.toResponse(savedCommission);
    }

    @Override
    @Transactional
    public CommissionResponse rejectCommission(Long id, CommissionStatusRequest request, Long approvedById) {
        Commission commission = commissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found with id: " + id));

        if (commission.getStatus() != CommissionStatus.PENDING) {
            throw new IllegalStateException("Commission can only be rejected from PENDING status");
        }

        User approver = userRepository.findById(approvedById)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found with id: " + approvedById));

        commission.setStatus(CommissionStatus.REJECTED);
        commission.setApprovedBy(approver);
        commission.setApprovedAt(LocalDateTime.now());
        commission.setRemarks(request.getRemarks() != null ? request.getRemarks() : commission.getRemarks());

        Commission savedCommission = commissionRepository.save(commission);

        notificationService.createNotification(
                commission.getUser().getId(),
                "Commission Rejected",
                "Your commission of " + commission.getAmount() + " has been rejected. Reason: " +
                        (request.getRemarks() != null ? request.getRemarks() : "N/A"),
                com.rksolutions.common.enums.NotificationType.COMMISSION,
                "COMMISSION",
                commission.getId()
        );

        return commissionMapper.toResponse(savedCommission);
    }
}
