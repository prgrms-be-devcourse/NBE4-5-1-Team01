package com.team1.beanstore.domain.order.dto;

import com.team1.beanstore.domain.order.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

public record OrderResponse(
        @Schema(description = "주문 ID", example = "101")
        @NonNull Long orderId,

        @Schema(description = "주문 상태", example = "CONFIRMED")
        @NonNull String orderStatus,

        @Schema(description = "총 주문 금액", example = "15000")
        int totalPrice
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderStatus().name(),
                order.getTotalPrice()
        );
    }
}