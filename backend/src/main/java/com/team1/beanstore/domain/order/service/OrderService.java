package com.team1.beanstore.domain.order.service;

import com.team1.beanstore.domain.order.OrderMapper;
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
    public void createOrder(String email, String address, String zipCode, Map<Long, Integer> productQuantities) {
        int maxRetries = 3;

        for (int i = 0; i < maxRetries; i++) {
            try {
                List<OrderItem> orderItems = orderItemService.createOrderItems(productQuantities);
                Order order = orderMapper.toOrder(email, address, zipCode, orderItems);
                orderRepository.save(order);
                return;

            } catch (OptimisticLockException e) {
                if (i == maxRetries - 1) {
                    throw new IllegalStateException("주문량이 많습니다 추후에 시도해주세요");
                }
            }
        }

    }
}