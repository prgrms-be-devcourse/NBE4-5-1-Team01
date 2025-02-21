package com.team1.beanstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.beanstore.domain.order.dto.OrderRequest;
import com.team1.beanstore.domain.product.ProductRepository;
import com.team1.beanstore.domain.product.entity.Product;
import com.team1.beanstore.domain.product.entity.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void createOrder_Success() throws Exception {

        OrderRequest request = new OrderRequest(
                "test@example.com",
                "123 Street",
                "12345",
                Map.of(productId1, 2, productId2, 1)
        );

        // when & then
        mockMvc.perform(post("/GCcoffee/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("주문 성공"))
                .andExpect(jsonPath("$.data.orderId").isNumber())
                .andExpect(jsonPath("$.data.orderStatus").value("PENDING"))
                .andExpect(jsonPath("$.data.totalPrice").isNumber());
    }

    @Test
    @DisplayName("주문 생성 실패 테스트 - 잘못된 이메일 형식")
    void createOrder_Failure_InvalidEmail() throws Exception {
        OrderRequest request = new OrderRequest(
                "",
                "123 Street",
                "12345",
                Map.of(productId1, 2, productId2, 1)
        );

        mockMvc.perform(post("/GCcoffee/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성 실패 테스트 - 주소 미입력")
    void createOrder_Failure_EmptyAddress() throws Exception {
        OrderRequest request = new OrderRequest(
                "test@example.com",
                "",
                "12345",
                Map.of(productId1, 2, productId2, 1)
        );

        mockMvc.perform(post("/GCcoffee/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성 실패 테스트 - 우편번호 미입력")
    void createOrder_Failure_EmptyZipCode() throws Exception {
        // given
        OrderRequest request = new OrderRequest(
                "test@example.com",
                "123 Street",
                "",
                Map.of(productId1, 2, productId2, 1)
        );

        // when & then
        mockMvc.perform(post("/GCcoffee/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성 실패 테스트 - 주문 상품 목록이 비어 있음")
    void createOrder_Failure_EmptyProductList() throws Exception {
        // given
        OrderRequest request = new OrderRequest(
                "test@example.com",
                "123 Street",
                "12345",
                Map.of()
        );

        // when & then
        mockMvc.perform(post("/GCcoffee/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}