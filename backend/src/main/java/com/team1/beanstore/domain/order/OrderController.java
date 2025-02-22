package com.team1.beanstore.domain.order;

import com.team1.beanstore.domain.order.dto.OrderRequest;
import com.team1.beanstore.domain.order.dto.OrderResponse;
import com.team1.beanstore.domain.order.service.OrderService;
import com.team1.beanstore.global.dto.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/GCcoffee")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public RsData<OrderResponse> createOrder(@RequestBody @Valid OrderRequest request) {
        OrderResponse response = orderService.createOrder(
                request.email(),
                request.address(),
                request.zipCode(),
                request.productQuantities()
        );
        return new RsData<>(
                "200-1",
                "주문 성공",
                response);
    }
}