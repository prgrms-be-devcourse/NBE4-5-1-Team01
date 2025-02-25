package com.team1.beanstore.global.init;

import com.team1.beanstore.domain.order.repository.OrderRepository;
import com.team1.beanstore.domain.order.service.OrderService;
import com.team1.beanstore.domain.product.ProductRepository;
import com.team1.beanstore.domain.product.entity.Product;
import com.team1.beanstore.domain.product.entity.ProductCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    private final ProductRepository productRepository;

    @Autowired
    @Lazy
    private BaseInitData self;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;

    @Bean
    @Order(1)
    public ApplicationRunner applicationRunner1() {
        return args -> {
            self.productInit();
        };
    }

    @Bean
    @Order(2)
    public ApplicationRunner applicationRunner2() {
        return args -> {
            self.orderInit();
        };
    }

    @Transactional
    public void productInit() {
        if (productRepository.count() > 0) {
            return;
        }

        for (int i = 1; i <= 12; i++) {
            productRepository.saveAndFlush(Product.builder()
                    .name("핸드드립 원두 " + i)
                    .description("과일 향과 부드러운 산미가 특징인 원두 " + i)
                    .price(12000 + (i * 500))
                    .imageUrl("example.jpg")
                    .inventory(10 + i)
                    .category(ProductCategory.HAND_DRIP)
                    .build());
        }

        for (int i = 1; i <= 12; i++) {
            productRepository.saveAndFlush(Product.builder()
                    .name("디카페인 원두 " + i)
                    .description("카페인 제거된 원두 " + i)
                    .price(13000 + (i * 400))
                    .imageUrl("example.jpg")
                    .inventory(8 + i)
                    .category(ProductCategory.DECAF)
                    .build());
        }

        for (int i = 1; i <= 12; i++) {
            productRepository.saveAndFlush(Product.builder()
                    .name("티 종류 " + i)
                    .description("풍부한 향과 깊은 맛을 가진 차 " + i)
                    .price(10000 + (i * 300))
                    .imageUrl("example.jpg")
                    .inventory(5 + i)
                    .category(ProductCategory.TEA)
                    .build());
        }

        System.out.println("현재 저장된 상품 수: " + productRepository.count());
    }

    @Transactional
    public void orderInit() {
        if (orderRepository.count() > 0) {
            return;
        }

        String email = "test%d@example.com";
        String address = "aaa";
        String zipCode = "bbb";


        for (int i = 1; i <= 35; i++) {
            Map<Long, Integer> productQuantities = Map.of((long) i, 1, (long) (i + 1), 1);
            orderService.createOrder(email.formatted(i), address + i, zipCode + i, productQuantities);
        }

        System.out.println("현재 저장된 주문 수: " + orderRepository.count());
    }

}
