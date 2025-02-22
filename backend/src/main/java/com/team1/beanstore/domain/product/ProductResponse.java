package com.team1.beanstore.domain.product;

import com.team1.beanstore.domain.product.entity.Product;
import com.team1.beanstore.domain.product.entity.ProductCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

public record ProductResponse(
        @Schema(description = "상품 ID", example = "1")
        @NonNull Long id,

        @Schema(description = "상품명", example = "아메리카노")
        @NonNull String name,

        @Schema(description = "상품 설명", example = "고소한 원두로 내린 아메리카노")
        @NonNull String description,

        @Schema(description = "상품 가격", example = "5000")
        @NonNull Integer price,

        @Schema(description = "상품 이미지 URL", example = "https://example.com/image.jpg")
        @NonNull String imageUrl,

        @Schema(description = "상품 재고", example = "100")
        @NonNull Integer inventory,

        @Schema(description = "상품 카테고리", example = "COFFEE")
        @NonNull ProductCategory category
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