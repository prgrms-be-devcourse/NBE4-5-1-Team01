package com.team1.beanstore.domain.product.entity;

import lombok.Getter;

@Getter
public enum ProductCategory {
    HAND_DRIP("핸드드립"),
    DECAF("디카페인"),
    TEA("티");

    private final String name;

    ProductCategory(String name) {
        this.name = name;
    }
}
