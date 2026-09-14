package com.rksolutions.order.mapper;

import com.rksolutions.order.dto.OrderItemResponse;
import com.rksolutions.order.dto.OrderResponse;
import com.rksolutions.order.entity.Order;
import com.rksolutions.order.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        if (order == null) return null;

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setTotalAmount(order.getTotalAmount());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setOrderStatus(order.getOrderStatus());
        response.setPaymentStatus(order.getPaymentStatus());

        if (order.getCustomer() != null) {
            response.setCustomerId(order.getCustomer().getId());
            response.setCustomerName(order.getCustomer().getName());
        }

        if (order.getCreatedBy() != null) {
            response.setCreatedById(order.getCreatedBy().getId());
            response.setCreatedByName(order.getCreatedBy().getName());
        }

        if (order.getItems() != null) {
            List<OrderItemResponse> itemResponses = order.getItems().stream()
                    .map(this::toItemResponse)
                    .collect(Collectors.toList());
            response.setItems(itemResponses);
        }

        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }

    public OrderItemResponse toItemResponse(OrderItem item) {
        if (item == null) return null;

        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setItemType(item.getItemType());
        response.setItemId(item.getItemId());
        response.setItemName(item.getItemName());
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice());
        response.setTotal(item.getTotal());
        return response;
    }
}
