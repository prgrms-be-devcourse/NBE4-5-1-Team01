package com.team1.beanstore.domain.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SearchKeywordType {
    id("id"),
    name("name"),
    description("description"),
    category("category");

    private final String value;
}