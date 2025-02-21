package com.team1.beanstore.domain.order;

import com.team1.beanstore.domain.order.entity.Order;
import com.team1.beanstore.domain.order.entity.OrderItem;
import com.team1.beanstore.domain.order.entity.OrderStatus;
import com.team1.beanstore.domain.order.repository.OrderRepository;
import com.team1.beanstore.domain.order.service.OrderService;
import com.team1.beanstore.domain.product.ProductRepository;
import com.team1.beanstore.domain.product.entity.Product;
import com.team1.beanstore.domain.product.entity.ProductCategory;
import com.team1.beanstore.global.dto.PageDto;
import com.team1.beanstore.global.exception.ServiceException;
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
                .description("과일 향과 부드러운 산미가 특징인 원두 먹고싶다")
                .price(12000)
                .imageUrl("대충 이미지.jpg")
                .inventory(10)
                .category(ProductCategory.HAND_DRIP)
                .build());

        Product product2 = productRepository.saveAndFlush(Product.builder()
                .name("콜롬비아 수프리모")
                .description("견과류와 초콜릿 향이 감도는 원두 먹고싶다")
                .price(13000)
                .imageUrl("멍충 이미지.jpg")
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
        assertThat(orderRepository.count()).isEqualTo(4);
        Order order = orderRepository.findAll().getLast();
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
        Map<Long, Integer> productQuantities = Map.of(productId2, 10);

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

        assertThat(product1.getInventory()).isEqualTo(8); // 8개 남음
        assertThat(product2.getInventory()).isEqualTo(4); // 4개 남음
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
        int expectedTotalPrice = (12000 * 2) + (13000);
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

    @Test
    @DisplayName("주문 생성 후 OrderItem 의 orderId가 정상적으로 할당되는지 검증")
    void createOrder_AssignOrderIdToOrderItems() {
        // given
        String email = "test@example.com";
        String address = "123 Street";
        String zipCode = "12345";
        Map<Long, Integer> productQuantities = Map.of(productId1, 2, productId2, 1);

        // when
        orderService.createOrder(email, address, zipCode, productQuantities);

        // then
        Order order = orderRepository.findAll().getFirst();
        assertThat(order.getOrderItems()).isNotEmpty();

        for (OrderItem item : order.getOrderItems()) {
            assertThat(item.getOrder()).isEqualTo(order);
            assertThat(item.getOrder().getId()).isEqualTo(order.getId());
        }
    }

    @Test
    @DisplayName("존재하지 않는 상품을 주문하면 예외 발생")
    void createOrder_Failure_NonExistentProduct() {
        // given
        String email = "test@example.com";
        String address = "123 Street";
        String zipCode = "12345";
        Map<Long, Integer> productQuantities = Map.of(9999L, 2);

        // when & then
        assertThrows(IllegalStateException.class, () ->
                orderService.createOrder(email, address, zipCode, productQuantities));
    }

    @Test
    @DisplayName("주문 생성 후 totalPrice 가 0이 아닌지 검증")
    void createOrder_Failure_ZeroTotalPrice() {
        // given
        String email = "test@example.com";
        String address = "123 Street";
        String zipCode = "12345";
        Map<Long, Integer> productQuantities = Map.of(productId1, 2, productId2, 1);

        // when
        orderService.createOrder(email, address, zipCode, productQuantities);

        // then
        Order order = orderRepository.findAll().getFirst();
        assertThat(order.getTotalPrice()).isGreaterThan(0);
    }

    private void setUpOrder() {
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
    }

    @Test
    @DisplayName("주문 전체 조회 - 성공")
    void getOrders1() {
        setUpOrder();

        int page = 1;
        int pageSize = 10;
        String keyword = "";
        String sort = "asc";

        PageDto<OrderResponseWithDetail> pageOrder = orderService.getOrders(page, pageSize, keyword, sort);

        assertThat(pageOrder.getItems().size()).isEqualTo(5);
        assertThat(pageOrder.getItems().getFirst().getEmail()).isEqualTo("test1@example.com");
    }

    @Test
    @DisplayName("주문 전체 조회 - 성공 - 페이징")
    void getOrders2() {
        setUpOrder();

        int page = 1;
        int pageSize = 1;
        String keyword = "";
        String sort = "asc";

        PageDto<OrderResponseWithDetail> pageOrder = orderService.getOrders(page, pageSize, keyword, sort);

        assertThat(pageOrder.getItems().size()).isEqualTo(1);
        assertThat(pageOrder.getItems().getFirst().getEmail()).isEqualTo("test1@example.com");
    }

    @Test
    @DisplayName("주문 전체 조회 - 성공 - 검색")
    void getOrders3() {
        setUpOrder();

        int page = 1;
        int pageSize = 10;
        String keyword = "test1";
        String sort = "asc";

        PageDto<OrderResponseWithDetail> pageOrder = orderService.getOrders(page, pageSize, keyword, sort);

        assertThat(pageOrder.getItems().size()).isEqualTo(2);
        assertThat(pageOrder.getItems().getFirst().getEmail()).isEqualTo("test1@example.com");
    }

    @Test
    @DisplayName("주문 전체 조회 - 성공 - 정렬")
    void getOrders4() {
        setUpOrder();

        int page = 1;
        int pageSize = 10;
        String keyword = "";
        String sort = "desc";

        PageDto<OrderResponseWithDetail> pageOrder = orderService.getOrders(page, pageSize, "", sort);

        assertThat(pageOrder.getItems().size()).isEqualTo(5);
        assertThat(pageOrder.getItems().getFirst().getEmail()).isEqualTo("test2@example.com");
    }

    @Test
    @DisplayName("주문 상세 조회 테스트 - 성공")
    void getOrder1() {
        setUpOrder();

        int page = 1;
        int pageSize = 10;
        String keyword = "";
        String sort = "desc";

        long id = orderRepository.findAll().getFirst().getId();

        OrderResponseWithDetail orderResponse = orderService.getOrder(id);
        assertThat(orderResponse.getEmail()).isEqualTo("test1@example.com");
        assertThat(orderResponse.getZipCode()).isEqualTo("aaa");
        assertThat(orderResponse.getTotalPrice()).isEqualTo(37000);
    }

    @Test
    @DisplayName("주문 상세 조회 테스트 - 실패 - 없는 주문 조회")
    void getOrder2() {
        setUpOrder();

        long id = 1000000;
        String email = "test@example.com";
        String address = "123 Street";
        String zipCode = "12345";
        Map<Long, Integer> productQuantities = Map.of(productId1, 2, productId2, 1);

        orderService.createOrder(email, address, zipCode, productQuantities);

        assertThrows(ServiceException.class,
                () -> orderService.getOrder(id));
    }

    @Test
    @DisplayName("주문 수정 테스트 - 성공")
    void modifyOrder1() {
        setUpOrder();

        long id = orderRepository.findAll().getFirst().getId();
        OrderStatus orderStatus = OrderStatus.COMPLETED;

        Order order = orderRepository.findById(id).get();
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING);

        orderService.modify(id, orderStatus);

        Order modifiedOrder = orderRepository.findById(id).get();
        assertThat(modifiedOrder.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("주문 수정 테스트 - 실패 - 없는 주문 수정")
    void modifyOrder2() {
        setUpOrder();

        long id = 100000;
        OrderStatus orderStatus = OrderStatus.COMPLETED;

        assertThrows(ServiceException.class,
                () -> orderService.modify(id, orderStatus));
    }

    @Test
    @DisplayName("주문 삭제 테스트 - 성공")
    void deleteOrder1() {
        setUpOrder();

        long id = orderRepository.findAll().getFirst().getId();

        orderService.delete(id);

        Order order = orderRepository.findAll().getFirst();

        assertThat(order.getId()).isNotEqualTo(id);
    }

    @Test
    @DisplayName("주문 삭제 테스트 - 실패 - 없는 주문 삭제")
    void deleteOrder2() {
        setUpOrder();

        long id = 100000;

        assertThrows(ServiceException.class,
                () -> orderService.delete(id));
    }
}