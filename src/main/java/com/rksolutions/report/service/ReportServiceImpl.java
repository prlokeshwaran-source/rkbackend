package com.rksolutions.report.service;

import com.rksolutions.commission.entity.Commission;
import com.rksolutions.commission.repository.CommissionRepository;
import com.rksolutions.common.enums.CommissionStatus;
import com.rksolutions.common.enums.CustomerStatus;
import com.rksolutions.common.enums.OrderStatus;
import com.rksolutions.common.enums.PaymentStatus;
import com.rksolutions.common.enums.UserStatus;
import com.rksolutions.customer.repository.CustomerRepository;
import com.rksolutions.order.entity.Order;
import com.rksolutions.order.repository.OrderRepository;
import com.rksolutions.payment.entity.Payment;
import com.rksolutions.payment.repository.PaymentRepository;
import com.rksolutions.report.dto.*;
import com.rksolutions.user.entity.User;
import com.rksolutions.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CommissionRepository commissionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public SalesReportDTO getSalesReport(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime start = fromDate != null ? fromDate.atStartOfDay() : LocalDateTime.now().minusDays(30).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = toDate != null ? toDate.atTime(23, 59, 59) : LocalDateTime.now();

        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> !o.getCreatedAt().isBefore(start) && !o.getCreatedAt().isAfter(end))
                .collect(Collectors.toList());

        BigDecimal totalSales = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<SalesReportItem> items = orders.stream()
                .map(o -> new SalesReportItem(
                        o.getId(),
                        o.getOrderNumber(),
                        o.getCustomer() != null ? o.getCustomer().getName() : null,
                        o.getTotalAmount(),
                        o.getCreatedAt(),
                        o.getPaymentStatus().name()
                ))
                .collect(Collectors.toList());

        SalesReportDTO dto = new SalesReportDTO();
        dto.setTotalSales(totalSales);
        dto.setTotalOrders((long) orders.size());
        dto.setTotalRevenue(totalSales);
        dto.setOrders(items);
        dto.setFromDate(fromDate);
        dto.setToDate(toDate);
        return dto;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public CommissionReportDTO getCommissionReport(LocalDate fromDate, LocalDate toDate, String status) {
        LocalDateTime start = fromDate != null ? fromDate.atStartOfDay() : LocalDateTime.now().minusDays(30).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = toDate != null ? toDate.atTime(23, 59, 59) : LocalDateTime.now();

        List<Commission> allCommissions = commissionRepository.findAll().stream()
                .filter(c -> !c.getCreatedAt().isBefore(start) && !c.getCreatedAt().isAfter(end))
                .collect(Collectors.toList());

        if (status != null) {
            try {
                CommissionStatus commissionStatus = CommissionStatus.valueOf(status.toUpperCase());
                allCommissions = allCommissions.stream()
                        .filter(c -> c.getStatus() == commissionStatus)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException ignored) {
            }
        }

        BigDecimal totalAmount = allCommissions.stream()
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pendingAmount = allCommissions.stream()
                .filter(c -> c.getStatus() == CommissionStatus.PENDING)
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal approvedAmount = allCommissions.stream()
                .filter(c -> c.getStatus() == CommissionStatus.APPROVED)
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal paidAmount = allCommissions.stream()
                .filter(c -> c.getStatus() == CommissionStatus.PAID)
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CommissionReportItem> items = allCommissions.stream()
                .map(c -> new CommissionReportItem(
                        c.getId(),
                        c.getUser() != null ? c.getUser().getName() : null,
                        c.getCustomer() != null ? c.getCustomer().getName() : null,
                        c.getOrder() != null ? c.getOrder().getId() : null,
                        c.getAmount(),
                        c.getStatus().name(),
                        c.getType().name(),
                        c.getCreatedAt()
                ))
                .collect(Collectors.toList());

        CommissionReportDTO dto = new CommissionReportDTO();
        dto.setTotalCommissions(totalAmount);
        dto.setTotalCommissionsCount((long) allCommissions.size());
        dto.setPendingAmount(pendingAmount);
        dto.setApprovedAmount(approvedAmount);
        dto.setPaidAmount(paidAmount);
        dto.setCommissions(items);
        return dto;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public MemberReportDTO getMemberReport() {
        List<User> allUsers = userRepository.findAll();

        long totalMembers = allUsers.size();
        long activeMembers = allUsers.stream().filter(u -> u.getStatus() == UserStatus.ACTIVE).count();
        long pendingMembers = allUsers.stream().filter(u -> u.getStatus() == UserStatus.PENDING).count();

        List<MemberReportItem> items = allUsers.stream()
                .map(u -> {
                    String roleName = u.getRoles() != null && !u.getRoles().isEmpty()
                            ? u.getRoles().iterator().next().getName().name() : null;

                    BigDecimal earnedCommission = commissionRepository.findByUserIdAndStatus(
                            u.getId(), CommissionStatus.APPROVED).stream()
                            .map(Commission::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    Long totalCommissionCount = (long) commissionRepository.findByUserId(u.getId()).size();

                    return new MemberReportItem(
                            u.getId(),
                            u.getName(),
                            u.getEmail(),
                            roleName,
                            u.getStatus().name(),
                            null,
                            totalCommissionCount,
                            earnedCommission,
                            u.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());

        MemberReportDTO dto = new MemberReportDTO();
        dto.setTotalMembers(totalMembers);
        dto.setActiveMembers(activeMembers);
        dto.setPendingMembers(pendingMembers);
        dto.setMembers(items);
        return dto;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public PerformanceReportDTO getPerformanceReport(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime start = fromDate != null ? fromDate.atStartOfDay() : LocalDateTime.now().minusDays(30).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = toDate != null ? toDate.atTime(23, 59, 59) : LocalDateTime.now();

        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> !o.getCreatedAt().isBefore(start) && !o.getCreatedAt().isAfter(end))
                .collect(Collectors.toList());

        BigDecimal totalSales = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCommissions = commissionRepository.findAll().stream()
                .filter(c -> !c.getCreatedAt().isBefore(start) && !c.getCreatedAt().isAfter(end))
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<User> users = userRepository.findAll();
        List<MemberPerformanceItem> topPerformers = users.stream()
                .map(u -> {
                    long ordersCount = orders.stream()
                            .filter(o -> o.getCreatedBy() != null && o.getCreatedBy().getId().equals(u.getId()))
                            .count();
                    BigDecimal userSales = orders.stream()
                            .filter(o -> o.getCreatedBy() != null && o.getCreatedBy().getId().equals(u.getId()))
                            .map(Order::getTotalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal userCommission = commissionRepository.findByUserId(u.getId()).stream()
                            .map(Commission::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new MemberPerformanceItem(u.getId(), u.getName(), ordersCount, userSales, userCommission);
                })
                .filter(item -> item.getOrdersCount() > 0 || item.getTotalSales().compareTo(BigDecimal.ZERO) > 0)
                .sorted((a, b) -> b.getTotalSales().compareTo(a.getTotalSales()))
                .limit(10)
                .collect(Collectors.toList());

        PerformanceReportDTO dto = new PerformanceReportDTO();
        dto.setTotalSales(totalSales);
        dto.setTotalCommissions(totalCommissions);
        dto.setTotalOrders((long) orders.size());
        dto.setTotalCustomers(customerRepository.count());
        dto.setTopPerformers(topPerformers);
        dto.setFromDate(fromDate);
        dto.setToDate(toDate);
        return dto;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public DailyReportDTO getDailyReport() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        List<Order> todayOrdersList = orderRepository.findAll().stream()
                .filter(o -> !o.getCreatedAt().isBefore(startOfDay) && !o.getCreatedAt().isAfter(endOfDay))
                .collect(Collectors.toList());

        BigDecimal todaySales = todayOrdersList.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long todayCustomers = customerRepository.findAll().stream()
                .filter(c -> !c.getCreatedAt().isBefore(startOfDay) && !c.getCreatedAt().isAfter(endOfDay))
                .count();

        long pendingFollowUps = 0L; // Would need FollowUpRepository injected
        BigDecimal pendingCommission = commissionRepository.findByStatus(CommissionStatus.PENDING).stream()
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DailyReportDTO dto = new DailyReportDTO();
        dto.setDate(LocalDate.now());
        dto.setTodaySales(todaySales);
        dto.setTodayOrders((long) todayOrdersList.size());
        dto.setTodayCustomers(todayCustomers);
        dto.setPendingFollowUps(pendingFollowUps);
        dto.setPendingCommission(pendingCommission);
        return dto;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public MonthlyReportDTO getMonthlyReport(Integer year, Integer month) {
        int targetYear = year != null ? year : LocalDateTime.now().getYear();
        int targetMonth = month != null ? month : LocalDateTime.now().getMonthValue();

        LocalDateTime start = LocalDateTime.of(targetYear, targetMonth, 1, 0, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);

        List<Order> monthOrders = orderRepository.findAll().stream()
                .filter(o -> !o.getCreatedAt().isBefore(start) && !o.getCreatedAt().isAfter(end))
                .collect(Collectors.toList());

        BigDecimal totalSales = monthOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalCustomers = customerRepository.findAll().stream()
                .filter(c -> !c.getCreatedAt().isBefore(start) && !c.getCreatedAt().isAfter(end))
                .count();

        Map<LocalDate, BigDecimal> dailySales = monthOrders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getCreatedAt().toLocalDate(),
                        Collectors.reducing(BigDecimal.ZERO, Order::getTotalAmount, BigDecimal::add)
                ));

        BigDecimal totalCommissions = commissionRepository.findAll().stream()
                .filter(c -> !c.getCreatedAt().isBefore(start) && !c.getCreatedAt().isAfter(end))
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        MonthlyReportDTO dto = new MonthlyReportDTO();
        dto.setYear(targetYear);
        dto.setMonth(targetMonth);
        dto.setTotalSales(totalSales);
        dto.setTotalOrders((long) monthOrders.size());
        dto.setTotalCustomers(totalCustomers);
        dto.setTotalCommissions(totalCommissions);
        dto.setDailySales(dailySales);
        return dto;
    }

    @Override
    public byte[] exportReport(String reportType, LocalDate fromDate, LocalDate toDate) {
        StringBuilder csv = new StringBuilder();
        csv.append("Report Type: ").append(reportType)
                .append(", From: ").append(fromDate)
                .append(", To: ").append(toDate)
                .append("\n");

        switch (reportType.toLowerCase()) {
            case "sales":
                SalesReportDTO sales = getSalesReport(fromDate, toDate);
                csv.append("OrderID,OrderNumber,Customer,Amount,Date,PaymentStatus\n");
                for (SalesReportItem item : sales.getOrders()) {
                    csv.append(item.getOrderId()).append(",")
                            .append(item.getOrderNumber()).append(",")
                            .append(item.getCustomerName()).append(",")
                            .append(item.getAmount()).append(",")
                            .append(item.getDate()).append(",")
                            .append(item.getPaymentStatus()).append("\n");
                }
                break;

            case "commission":
                CommissionReportDTO commission = getCommissionReport(fromDate, toDate, null);
                csv.append("CommissionID,UserName,Customer,OrderID,Amount,Status,Type,Date\n");
                for (CommissionReportItem item : commission.getCommissions()) {
                    csv.append(item.getCommissionId()).append(",")
                            .append(item.getUserName()).append(",")
                            .append(item.getCustomerName()).append(",")
                            .append(item.getOrderId()).append(",")
                            .append(item.getAmount()).append(",")
                            .append(item.getStatus()).append(",")
                            .append(item.getType()).append(",")
                            .append(item.getCreatedAt()).append("\n");
                }
                break;

            case "members":
                MemberReportDTO members = getMemberReport();
                csv.append("UserID,Name,Email,Role,Status,DirectCustomers,TotalCommissions,EarnedCommission,JoinedAt\n");
                for (MemberReportItem item : members.getMembers()) {
                    csv.append(item.getUserId()).append(",")
                            .append(item.getUserName()).append(",")
                            .append(item.getEmail()).append(",")
                            .append(item.getRole()).append(",")
                            .append(item.getStatus()).append(",")
                            .append(item.getDirectCustomers()).append(",")
                            .append(item.getTotalCommissions()).append(",")
                            .append(item.getEarnedCommission()).append(",")
                            .append(item.getJoinedAt()).append("\n");
                }
                break;

            default:
                csv.append("No data available for report type: ").append(reportType);
        }

        return csv.toString().getBytes();
    }
}
