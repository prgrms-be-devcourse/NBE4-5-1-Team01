package com.team1.beanstore.domain.order.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("주문 대기"),
    COMPLETED("주문 완료"),
    CANCEL("주문 취소");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }
}