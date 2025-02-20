package com.team1.beanstore.domain.order;

import com.team1.beanstore.domain.order.entity.Order;
import com.team1.beanstore.domain.order.entity.OrderItem;
import com.team1.beanstore.domain.order.entity.OrderStatus;
import com.team1.beanstore.domain.product.entity.Product;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderMapper {

    public OrderItem toOrderItem(Product product, int quantity) {
        return OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .totalPrice(product.getPrice() * quantity)
                .build();
    }

    public Order toOrder(String email, String address, String zipCode, List<OrderItem> orderItems) {
        return Order.builder()
                .email(email)
                .address(address)
                .zipCode(zipCode)
                .orderStatus(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .orderItems(orderItems)
                .build();
    }
}