package com.team1.beanstore.domain.order.service;

import com.team1.beanstore.domain.order.OrderMapper;
import com.team1.beanstore.domain.order.OrderResponse;
import com.team1.beanstore.domain.order.entity.Order;
import com.team1.beanstore.domain.order.entity.OrderItem;
import com.team1.beanstore.domain.order.repository.OrderRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderItemService orderItemService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(String email, String address, String zipCode, Map<Long, Integer> productQuantities) {
        try {
            Order order = orderMapper.toOrder(email, address, zipCode);
            List<OrderItem> orderItems = orderItemService.createOrderItems(order, productQuantities);
            order.addOrderItems(orderItems);
            orderRepository.save(order);
            return OrderResponse.from(order);
        } catch (OptimisticLockException e) {
            throw new IllegalStateException("동시 주문이 많아 처리가 실패했습니다. 다시 시도해주세요.");
        }
    }
}