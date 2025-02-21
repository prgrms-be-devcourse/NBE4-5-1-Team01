package com.team1.beanstore.global.init;

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

    private final OrderService orderService;
    private final ProductRepository productRepository;

    @Autowired
    @Lazy
    private BaseInitData self;

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
    public void orderInit() {
        if (orderService.count() > 0) {
            return;
        }

        Long productId1 = 1L;
        Long productId2 = 2L;
        Long productId3 = 3L;


        String email1 = "test1@example.com";
        String address1 = "aaa";
        String zipCode1 = "aaa";
        Map<Long, Integer> productQuantities1 = Map.of(productId1, 2, productId2, 1);
        orderService.createOrder(email1, address1, zipCode1, productQuantities1);

        String email2 = "test2@example.com";
        String address2 = "bbb";
        String zipCode2 = "bbb";
        Map<Long, Integer> productQuantities2 = Map.of(productId1, 1, productId2, 2);
        orderService.createOrder(email2, address2, zipCode2, productQuantities2);

        Map<Long, Integer> productQuantities3 = Map.of(productId1, 3, productId2, 1, productId3, 1);
        orderService.createOrder(email2, address2, zipCode2, productQuantities3);
    }

    @Transactional
    public void productInit() {

        if (productRepository.count() > 0) {
            return;
        }

        Product product1 = productRepository.saveAndFlush(Product.builder()
                .name("에티오피아 예가체프")
                .description("과일 향과 부드러운 산미가 특징인 원두")
                .price(12000)
                .imageUrl("ethiopia.jpg")
                .inventory(10)
                .category(ProductCategory.HAND_DRIP)
                .build());

        Product product2 = productRepository.saveAndFlush(Product.builder()
                .name("콜롬비아 수프리모")
                .description("견과류와 초콜릿 향이 감도는 원두")
                .price(13000)
                .imageUrl("colombia.jpg")
                .inventory(5)
                .category(ProductCategory.DECAF)
                .build());

        Product product3 = productRepository.saveAndFlush(Product.builder()
                .name("케냐 AA")
                .description("적당한 산미와 고급스러운 원두")
                .price(17000)
                .imageUrl("kenya.jpg")
                .inventory(15)
                .category(ProductCategory.HAND_DRIP)
                .build());
    }
}
