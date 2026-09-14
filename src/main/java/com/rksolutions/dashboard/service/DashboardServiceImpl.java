package com.rksolutions.dashboard.service;

import com.rksolutions.common.enums.CommissionStatus;
import com.rksolutions.common.enums.CustomerStatus;
import com.rksolutions.common.enums.OrderStatus;
import com.rksolutions.common.enums.PaymentStatus;
import com.rksolutions.common.enums.UserStatus;
import com.rksolutions.commission.entity.Commission;
import com.rksolutions.commission.repository.CommissionRepository;
import com.rksolutions.customer.repository.CustomerRepository;
import com.rksolutions.dashboard.dto.DashboardResponse;
import com.rksolutions.followup.entity.FollowUp;
import com.rksolutions.followup.repository.FollowUpRepository;
import com.rksolutions.order.entity.Order;
import com.rksolutions.order.repository.OrderRepository;
import com.rksolutions.payment.entity.Payment;
import com.rksolutions.payment.repository.PaymentRepository;
import com.rksolutions.user.entity.User;
import com.rksolutions.user.repository.UserRepository;
import com.rksolutions.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CommissionRepository commissionRepository;

    @Autowired
    private FollowUpRepository followUpRepository;

    @Autowired
    private WalletRepository walletRepository;

    private LocalDateTime getStartOfDay() {
        return LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
    }

    private LocalDateTime getEndOfDay() {
        return LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public DashboardResponse getUserDashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new com.rksolutions.common.exception.ResourceNotFoundException("User not found"));

        DashboardResponse response = new DashboardResponse();
        response.setTotalCustomers((long) customerRepository.findByAssignedTo(user).size());
        response.setPendingCalls((long) followUpRepository.findByAssignedToAndStatus(
                user, com.rksolutions.common.enums.FollowUpStatus.PENDING).size());

        List<Commission> pendingCommissions = commissionRepository.findByUserIdAndStatus(
                userId, CommissionStatus.PENDING);
        response.setPendingCommission(pendingCommissions.stream()
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        response.setTotalMembers(0L);
        response.setTotalRevenue(BigDecimal.ZERO);
        response.setTotalCommissionPending(BigDecimal.ZERO);
        response.setTotalWalletBalance(BigDecimal.ZERO);
        response.setPendingUsers(0L);
        response.setTodayOrders(0L);
        response.setTodayRevenue(BigDecimal.ZERO);
        response.setTotalOrders(0L);
        response.setTotalPayments(0L);
        response.setTotalCommissions((long) commissionRepository.findByUserId(userId).size());

        return response;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public DashboardResponse getAdminDashboard() {
        DashboardResponse response = new DashboardResponse();

        response.setTotalMembers(userRepository.count());
        response.setTotalCustomers(customerRepository.count());
        response.setTotalOrders(orderRepository.count());
        response.setTotalPayments(paymentRepository.count());
        response.setPendingUsers((long) userRepository.findByStatus(UserStatus.PENDING).size());

        LocalDateTime startOfDay = getStartOfDay();
        LocalDateTime endOfDay = getEndOfDay();

        List<Order> todayOrders = orderRepository.findAll().stream()
                .filter(o -> !o.getCreatedAt().isBefore(startOfDay) && !o.getCreatedAt().isAfter(endOfDay))
                .collect(java.util.stream.Collectors.toList());
        response.setTodayOrders((long) todayOrders.size());

        BigDecimal todayRevenue = todayOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTodayRevenue(todayRevenue);

        List<Commission> pendingCommissions = commissionRepository.findByStatus(CommissionStatus.PENDING);
        response.setPendingCommission(pendingCommissions.stream()
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        response.setTotalCommissionPending(response.getPendingCommission());

        response.setTotalRevenue(paymentRepository.findAll().stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.SUCCESS)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        BigDecimal totalWalletBalance = walletRepository.findAll().stream()
                .map(w -> w.getBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTotalWalletBalance(totalWalletBalance);

        response.setTotalCommissions((long) commissionRepository.count());
        response.setPendingCalls((long) followUpRepository.findAll().stream()
                .filter(f -> f.getStatus() == com.rksolutions.common.enums.FollowUpStatus.PENDING)
                .count());

        response.setTotalCustomers(customerRepository.count());

        return response;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public DashboardResponse getManagerDashboard(Long userId) {
        User manager = userRepository.findById(userId)
                .orElseThrow(() -> new com.rksolutions.common.exception.ResourceNotFoundException("User not found"));

        List<User> teamMembers = userRepository.findAll().stream()
                .filter(u -> u.getManager() != null && u.getManager().getId().equals(userId))
                .collect(java.util.stream.Collectors.toList());

        DashboardResponse response = new DashboardResponse();
        response.setTotalMembers((long) teamMembers.size());

        long teamCustomers = teamMembers.stream()
                .mapToLong(u -> customerRepository.findByAssignedTo(u).size())
                .sum();
        response.setTotalCustomers(teamCustomers);

        long teamFollowUps = teamMembers.stream()
                .mapToLong(u -> followUpRepository.findByAssignedToAndStatus(
                        u, com.rksolutions.common.enums.FollowUpStatus.PENDING).size())
                .sum();
        response.setPendingCalls(teamFollowUps);

        List<Commission> teamCommissions = teamMembers.stream()
                .flatMap(u -> commissionRepository.findByUserIdAndStatus(
                        u.getId(), CommissionStatus.PENDING).stream())
                .collect(java.util.stream.Collectors.toList());
        response.setPendingCommission(teamCommissions.stream()
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        response.setTotalRevenue(BigDecimal.ZERO);
        response.setTodayOrders(0L);
        response.setTodayRevenue(BigDecimal.ZERO);
        response.setTotalCommissionPending(response.getPendingCommission());
        response.setTotalWalletBalance(BigDecimal.ZERO);
        response.setPendingUsers(0L);
        response.setTotalOrders(0L);
        response.setTotalPayments(0L);
        response.setTotalCommissions((long) teamCommissions.size() + teamCommissions.size());

        return response;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public DashboardResponse getSuperAdminDashboard() {
        return getAdminDashboard();
    }
}
