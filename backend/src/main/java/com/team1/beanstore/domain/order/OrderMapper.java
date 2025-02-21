package com.team1.beanstore.domain.order;

import com.team1.beanstore.domain.order.entity.Order;
import com.team1.beanstore.domain.order.entity.OrderItem;
import com.team1.beanstore.domain.order.entity.OrderStatus;
import com.team1.beanstore.domain.product.entity.Product;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderMapper {

    public OrderItem toOrderItem(Order order, Product product, int quantity) {
        return OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(quantity)
                .totalPrice(product.getPrice() * quantity)
                .build();
    }

    public Order toOrder(String email, String address, String zipCode) {
        return Order.builder()
                .email(email)
                .address(address)
                .zipCode(zipCode)
                .orderStatus(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .totalPrice(0)
                .build();
    }
}
