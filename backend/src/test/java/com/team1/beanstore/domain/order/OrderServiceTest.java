package com.team1.beanstore.domain.order;

import com.team1.beanstore.domain.order.entity.Order;
import com.team1.beanstore.domain.order.repository.OrderRepository;
import com.team1.beanstore.domain.order.service.OrderService;
import com.team1.beanstore.domain.product.ProductRepository;
import com.team1.beanstore.domain.product.entity.Product;
import com.team1.beanstore.domain.product.entity.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    private Long productId1;
    private Long productId2;

    @BeforeEach
    void setUp() {
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

        this.productId1 = product1.getId();
        this.productId2 = product2.getId();
    }

    @Test
    @DisplayName("주문 생성 성공 테스트")
    void createOrder_Success() {
        // given
        String email = "test@example.com";
        String address = "123 Street";
        String zipCode = "12345";
        Map<Long, Integer> productQuantities = Map.of(productId1, 2, productId2, 1);

        // when
        assertDoesNotThrow(() -> orderService.createOrder(email, address, zipCode, productQuantities));

        // then
        assertThat(orderRepository.count()).isEqualTo(1);
        Order order = orderRepository.findAll().getFirst();
        assertThat(order.getEmail()).isEqualTo(email);
        assertThat(order.getOrderItems()).hasSize(2);
    }

    @Test
    @DisplayName("주문 생성 실패 테스트 - 재고 부족")
    void createOrder_Failure_InsufficientStock() {
        // given
        String email = "test@example.com";
        String address = "123 Street";
        String zipCode = "12345";
        Map<Long, Integer> productQuantities = Map.of(productId2, 10); // 재고: 5개인데 10개 주문

        // when & then
        assertThrows(IllegalStateException.class, () -> orderService.createOrder(email, address, zipCode, productQuantities));
    }

    @Test
    @DisplayName("주문 생성 시 재고 감소 테스트")
    void createOrder_DecreaseInventory() {
        // given
        String email = "test@example.com";
        String address = "123 Street";
        String zipCode = "12345";
        Map<Long, Integer> productQuantities = Map.of(productId1, 2, productId2, 1);

        // when
        orderService.createOrder(email, address, zipCode, productQuantities);

        // then
        Product product1 = productRepository.findById(productId1).orElseThrow();
        Product product2 = productRepository.findById(productId2).orElseThrow();

        assertThat(product1.getInventory()).isEqualTo(8); // 10 - 2개 주문 후 8개 남음
        assertThat(product2.getInventory()).isEqualTo(4); // 5 - 1개 주문 후 4개 남음
    }

    @Test
    @DisplayName("주문 생성 시 총 주문 가격이 정확하게 계산되는지 확인")
    void createOrder_CalculateTotalPrice() {
        // given
        String email = "test@example.com";
        String address = "123 Street";
        String zipCode = "12345";
        Map<Long, Integer> productQuantities = Map.of(productId1, 2, productId2, 1);

        // when
        orderService.createOrder(email, address, zipCode, productQuantities);

        // then
        Order order = orderRepository.findAll().getFirst();
        int expectedTotalPrice = (12000 * 2) + (13000); // 24000 + 13000 = 37000
        assertThat(order.getTotalPrice()).isEqualTo(expectedTotalPrice);
    }

    @Test
    @DisplayName("논리 삭제된 상품이 주문되지 않도록 검증")
    void createOrder_Failure_DeletedProduct() {
        // given
        Product product = productRepository.findById(productId1).orElseThrow();
        product.delete();
        productRepository.saveAndFlush(product);

        Map<Long, Integer> productQuantities = Map.of(productId1, 2);

        // when & then
        assertThrows(IllegalStateException.class, () ->
                orderService.createOrder("test@example.com", "123 Street", "12345", productQuantities));
    }
}