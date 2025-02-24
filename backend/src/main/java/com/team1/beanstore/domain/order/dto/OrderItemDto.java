package com.team1.beanstore.domain.order.dto;

import com.team1.beanstore.domain.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.NonNull;

@AllArgsConstructor
@Getter
public class OrderItemDto {
    @NonNull
    private final Long productId;
    @NonNull
    private final String productName;
    @NonNull
    private final int quantity;
    @NonNull
    private final int totalPrice;

    public OrderItemDto(OrderItem orderItem) {
        this(orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getTotalPrice());
    }
}
