package com.team1.beanstore.domain.order;

import com.team1.beanstore.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/GCcoffee")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("orders")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody @Valid OrderRequest request) {
        OrderResponse response = orderService.createOrder(
                request.email(),
                request.address(),
                request.zipCode(),
                request.productQuantities()
        );

        return ResponseEntity.ok(
                Map.of(
                        "status", 200,
                        "msg", "주문 성공",
                        "order", response
                )
        );
    }
}