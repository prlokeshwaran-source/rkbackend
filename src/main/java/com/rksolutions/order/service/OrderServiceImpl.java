package com.rksolutions.order.service;

import com.rksolutions.common.enums.ItemType;
import com.rksolutions.common.enums.OrderStatus;
import com.rksolutions.common.enums.PaymentStatus;
import com.rksolutions.customer.entity.Customer;
import com.rksolutions.customer.repository.CustomerRepository;
import com.rksolutions.handbook.entity.Handbook;
import com.rksolutions.handbook.repository.HandbookRepository;
import com.rksolutions.membership.entity.MembershipPlan;
import com.rksolutions.membership.repository.MembershipPlanRepository;
import com.rksolutions.order.dto.OrderItemRequest;
import com.rksolutions.order.dto.OrderRequest;
import com.rksolutions.order.dto.OrderResponse;
import com.rksolutions.order.entity.Order;
import com.rksolutions.order.entity.OrderItem;
import com.rksolutions.order.mapper.OrderMapper;
import com.rksolutions.order.repository.OrderRepository;
import com.rksolutions.common.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private HandbookRepository handbookRepository;

    @Autowired
    private MembershipPlanRepository membershipPlanRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomer(customer);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(request.getPaymentStatus() != null ? request.getPaymentStatus() : PaymentStatus.PENDING);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setItemType(itemRequest.getItemType());
            item.setItemId(itemRequest.getItemId());
            item.setQuantity(itemRequest.getQuantity());
            item.setItemName(itemRequest.getItemName());

            BigDecimal price = resolveItemPrice(itemRequest);
            item.setPrice(price);

            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            item.setTotal(itemTotal);

            total = total.add(itemTotal);
            items.add(item);
        }

        order.setItems(items);
        order.setTotalAmount(total);
        order.setDiscountAmount(BigDecimal.ZERO);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    private BigDecimal resolveItemPrice(OrderItemRequest itemRequest) {
        if (itemRequest.getPrice() != null && itemRequest.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            return itemRequest.getPrice();
        }

        if (itemRequest.getItemType() == ItemType.HANDBOOK) {
            Handbook handbook = handbookRepository.findById(itemRequest.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Handbook not found with id: " + itemRequest.getItemId()));
            return handbook.getPrice();
        } else if (itemRequest.getItemType() == ItemType.MEMBERSHIP) {
            MembershipPlan plan = membershipPlanRepository.findById(itemRequest.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with id: " + itemRequest.getItemId()));
            return plan.getPrice();
        }

        throw new IllegalArgumentException("Unknown item type: " + itemRequest.getItemType());
    }

    private String generateOrderNumber() {
        String datePart = java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuidPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + datePart + "-" + uuidPart;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<OrderResponse> getAllOrders(OrderStatus orderStatus, PaymentStatus paymentStatus) {
        Specification<Order> spec = Specification.where((Specification<Order>) null);

        if (orderStatus != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("orderStatus"), orderStatus));
        }
        if (paymentStatus != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("paymentStatus"), paymentStatus));
        }

        return orderRepository.findAll(spec).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Page<OrderResponse> getAllOrdersPaginated(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setOrderStatus(status);
        Order saved = orderRepository.save(order);
        return orderMapper.toResponse(saved);
    }

    @Override
    public OrderResponse updatePaymentStatus(Long id, PaymentStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setPaymentStatus(status);
        Order saved = orderRepository.save(order);
        return orderMapper.toResponse(saved);
    }
}
