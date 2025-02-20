package com.team1.beanstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.beanstore.domain.order.OrderRequest;
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

    @Test
    @DisplayName("주문 생성 성공 테스트")
    void createOrder_Success() throws Exception {
        OrderRequest request = new OrderRequest(
                "test@example.com",
                "123 Street",
                "12345",
                Map.of(1L, 2, 2L, 1)
        );

        mockMvc.perform(post("/GCcoffee/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주문 생성 실패 테스트 - 잘못된 이메일 형식")
    void createOrder_Failure_InvalidEmail() throws Exception {
        OrderRequest request = new OrderRequest(
                "",
                "123 Street",
                "12345",
                Map.of(1L, 2, 2L, 1)
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
                Map.of(1L, 2, 2L, 1)
        );

        mockMvc.perform(post("/GCcoffee/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성 실패 테스트 - 우편번호 미입력")
    void createOrder_Failure_EmptyZipCode() throws Exception {
        OrderRequest request = new OrderRequest(
                "test@example.com",
                "123 Street",
                "",
                Map.of(1L, 2, 2L, 1)
        );

        mockMvc.perform(post("/GCcoffee/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성 실패 테스트 - 주문 상품 목록이 비어 있음")
    void createOrder_Failure_EmptyProductList() throws Exception {
        OrderRequest request = new OrderRequest(
                "test@example.com",
                "123 Street",
                "12345",
                Map.of()
        );

        mockMvc.perform(post("/GCcoffee/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
