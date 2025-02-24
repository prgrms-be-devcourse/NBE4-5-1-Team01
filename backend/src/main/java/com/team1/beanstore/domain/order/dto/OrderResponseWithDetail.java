package com.team1.beanstore.domain.order.dto;

import com.team1.beanstore.domain.order.entity.Order;
import com.team1.beanstore.domain.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class OrderResponseWithDetail {
    @NonNull
    private final long id;
    @NonNull
    private final String email;
    @NonNull
    private final String address;
    @NonNull
    private final String zipCode;
    @NonNull
    private final LocalDateTime orderDate;
    @NonNull
    private final OrderStatus orderStatus;
    @NonNull
    private final int totalPrice;

    public OrderResponseWithDetail(Order order) {
        this(order.getId(),
                order.getEmail(),
                order.getAddress(),
                order.getZipCode(),
                order.getOrderDate(),
                order.getOrderStatus(),
                order.getTotalPrice());
    }
}
