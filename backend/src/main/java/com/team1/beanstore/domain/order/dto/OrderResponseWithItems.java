package com.team1.beanstore.domain.order.dto;

import com.team1.beanstore.domain.order.entity.Order;
import com.team1.beanstore.domain.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
public class OrderResponseWithItems {
    private final long orderId;
    private final String email;
    private final String address;
    private final String zipCode;
    private final LocalDateTime orderDate;
    private final OrderStatus orderStatus;
    private final int totalPrice;
    private final List<OrderItemDto> items;

    public OrderResponseWithItems(Order order) {
        this(order.getId(),
                order.getEmail(),
                order.getAddress(),
                order.getZipCode(),
                order.getOrderDate(),
                order.getOrderStatus(),
                order.getTotalPrice(),
                order.getOrderItems().stream().map(OrderItemDto::new).toList());
    }
}