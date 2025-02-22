package com.team1.beanstore.domain.admin;

import com.team1.beanstore.domain.order.dto.OrderResponse;
import com.team1.beanstore.domain.order.entity.OrderStatus;
import com.team1.beanstore.domain.order.service.OrderService;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class AdminControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    private OrderResponse orderResponse1;
    private OrderResponse orderResponse2;

    @BeforeEach
    void before() {
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
                .inventory(10)
                .category(ProductCategory.DECAF)
                .build());

        Long productId1 = product1.getId();
        Long productId2 = product2.getId();

        String email1 = "test1@example.com";
        String address1 = "aaa";
        String zipCode1 = "aaa";
        Map<Long, Integer> productQuantities1 = Map.of(productId1, 2, productId2, 1);
        orderResponse1 = orderService.createOrder(email1, address1, zipCode1, productQuantities1);

        String email2 = "test2@example.com";
        String address2 = "bbb";
        String zipCode2 = "bbb";
        Map<Long, Integer> productQuantities2 = Map.of(productId1, 1, productId2, 2);
        orderResponse2 = orderService.createOrder(email2, address2, zipCode2, productQuantities2);
    }

    @Test
    @DisplayName("전체 주문 조회 - 성공")
    void getOrders1() throws Exception {

        ResultActions resultActions = mvc
                .perform(get("/GCcoffee/admin/orders"))
                .andDo(print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminOrderController.class))
                .andExpect(handler().methodName("getOrders"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("주문 전체 조회가 완료되었습니다."))
                .andExpect(jsonPath("$.data.items[0].email").value("test1@example.com"))
                .andExpect(jsonPath("$.data.items[1].email").value("test2@example.com"));
    }

    @Test
    @DisplayName("상세 주문 조회 - 성공")
    void getOrder1() throws Exception {
        long id = orderResponse1.orderId();

        ResultActions resultActions = mvc
                .perform(get("/GCcoffee/admin/orders/%d".formatted(id)))
                .andDo(print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminOrderController.class))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 주문 상세 조회가 완료되었습니다.".formatted(id)))
                .andExpect(jsonPath("$.data.email").value("test1@example.com"))
                .andExpect(jsonPath("$.data.items[0].productName").value("에티오피아 예가체프"))
                .andExpect(jsonPath("$.data.items[0].quantity").value(2))
                .andExpect(jsonPath("$.data.items[1].productName").value("콜롬비아 수프리모"))
                .andExpect(jsonPath("$.data.items[1].quantity").value(1));
    }

    @Test
    @DisplayName("상세 주문 조회 - 실패 - 존재하지 않는 주문")
    void getOrder2() throws Exception {
        long id = 100000;

        ResultActions resultActions = mvc
                .perform(get("/GCcoffee/admin/orders/%d".formatted(id)))
                .andDo(print());

        resultActions
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(AdminOrderController.class))
                .andExpect(handler().methodName("getOrder"))
                .andExpect(jsonPath("$.code").value("404-1"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 주문입니다."));
    }

    @Test
    @DisplayName("주문 상태 수정 - 성공")
    void orderModify1() throws Exception {
        long id = orderResponse2.orderId();
        OrderStatus orderStatus = OrderStatus.COMPLETED;

        ResultActions resultActions = mvc
                .perform(patch("/GCcoffee/admin/orders/%d".formatted(id))
                        .content("""
                                {
                                    "orderStatus": "%s"
                                }
                                """
                                .formatted(orderStatus.name())
                                .stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)))
                .andDo(print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminOrderController.class))
                .andExpect(handler().methodName("modifyOrder"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 주문 수정이 완료되었습니다.".formatted(id)))
                .andExpect(jsonPath("$.data.orderId").value(id))
                .andExpect(jsonPath("$.data.orderStatus").value(orderStatus.name()));
    }

    @Test
    @DisplayName("주문 상태 수정 - 실패 - 존재하지 않는 주문")
    void orderModify2() throws Exception {
        long id = 100000;
        OrderStatus orderStatus = OrderStatus.COMPLETED;

        ResultActions resultActions = mvc
                .perform(patch("/GCcoffee/admin/orders/%d".formatted(id))
                        .content("""
                                {
                                    "orderStatus": "%s"
                                }
                                """
                                .formatted(orderStatus.name())
                                .stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)))
                .andDo(print());

        resultActions
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(AdminOrderController.class))
                .andExpect(handler().methodName("modifyOrder"))
                .andExpect(jsonPath("$.code").value("404-1"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 주문입니다."));
    }

    @Test
    @DisplayName("주문 삭제 - 성공")
    void deleteOrder1() throws Exception {
        long id = orderResponse1.orderId();

        ResultActions resultActions = mvc
                .perform(delete("/GCcoffee/admin/orders/%d".formatted(id)))
                .andDo(print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminOrderController.class))
                .andExpect(handler().methodName("deleteOrder"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 주문 삭제가 완료되었습니다.".formatted(id)));
    }

    @Test
    @DisplayName("주문 삭제 - 실패 - 존재하지 않는 주문")
    void deleteOrder2() throws Exception {
        long id = 100000;

        ResultActions resultActions = mvc
                .perform(delete("/GCcoffee/admin/orders/%d".formatted(id)))
                .andDo(print());

        resultActions
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(AdminOrderController.class))
                .andExpect(handler().methodName("deleteOrder"))
                .andExpect(jsonPath("$.code").value("404-1"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 주문입니다."));
    }
}
