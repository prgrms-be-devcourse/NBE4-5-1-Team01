package com.team1.beanstore.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record ItemReqBody(
        @Schema(description = "상품명", example = "에티오피아 예가체프")
        @NonNull @NotBlank String name,

        @Schema(description = "가격", example = "12000")
        @Min(1) int price,

        @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
        @NonNull @NotBlank String imageUrl,

        @Schema(description = "재고 수량", example = "50")
        int inventory,

        @Schema(description = "상품 설명", example = "과일 향과 부드러운 산미가 특징인 원두")
        @NonNull @NotBlank String description,

        @Schema(description = "카테고리", example = "HAND_DRIP")
        @NonNull @NotBlank String category
) {}