package com.team1.beanstore.domain.order.dto;

import com.team1.beanstore.domain.order.entity.Order;

public record OrderResponse(
        Long orderId,
        String orderStatus,
        int totalPrice
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderStatus().name(),
                order.getTotalPrice()
        );
    }
}