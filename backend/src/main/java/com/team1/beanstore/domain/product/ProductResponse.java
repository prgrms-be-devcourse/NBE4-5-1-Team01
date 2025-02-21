package com.team1.beanstore.domain.product;

import com.team1.beanstore.domain.product.entity.Product;
import com.team1.beanstore.domain.product.entity.ProductCategory;

public record ProductResponse(
        Long id,
        String name,
        String description,
        Integer price,
        String imageUrl,
        Integer inventory,
        ProductCategory category
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.getInventory(),
                product.getCategory()
        );
    }
}