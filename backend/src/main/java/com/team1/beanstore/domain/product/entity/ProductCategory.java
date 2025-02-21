package com.team1.beanstore.domain.product.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonCreator
    public static ProductCategory from(String value) {
        for (ProductCategory category : ProductCategory.values()) {
            if (category.name.equals(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("잘못된 카테고리 값입니다: " + value);
    }
}