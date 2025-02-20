package com.team1.beanstore.domain.order.service;

import com.team1.beanstore.domain.order.OrderMapper;
import com.team1.beanstore.domain.order.entity.OrderItem;
import com.team1.beanstore.domain.product.ProductRepository;
import com.team1.beanstore.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public List<OrderItem> createOrderItems(Map<Long, Integer> productQuantities) {
        List<Product> products = productRepository.findByIds(new ArrayList<>(productQuantities.keySet()));

        return products.stream()
                .map(product -> {
                    int quantity = productQuantities.get(product.getId());
                    product.decreaseInventory(quantity);
                    return orderMapper.toOrderItem(product, quantity);
                })
                .collect(Collectors.toList());
    }
}