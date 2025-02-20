package com.team1.beanstore.domain.order.service;

import com.team1.beanstore.domain.order.OrderMapper;
import com.team1.beanstore.domain.order.entity.Order;
import com.team1.beanstore.domain.order.entity.OrderItem;
import com.team1.beanstore.domain.product.ProductRepository;
import com.team1.beanstore.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public List<OrderItem> createOrderItems(Order order, Map<Long, Integer> productQuantities) {
        List<Product> products = productRepository.findByIds(new ArrayList<>(productQuantities.keySet()));

        if (products.size() != productQuantities.size()) {
            throw new IllegalStateException("주문할 수 없는 상품이 포함되어 있습니다.");
        }

        return products.stream()
                .map(product -> {
                    int quantity = productQuantities.get(product.getId());
                    product.decreaseInventory(quantity);
                    return orderMapper.toOrderItem(order, product, quantity);
                })
                .toList();
    }
}