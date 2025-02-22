package com.team1.beanstore.domain.order.dto;

import com.team1.beanstore.domain.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OrderItemDto {
    private final Long productId;
    private final String productName;
    private final int quantity;
    private final int totalPrice;

    public OrderItemDto(OrderItem orderItem) {
        this(orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getTotalPrice());
    }
}
