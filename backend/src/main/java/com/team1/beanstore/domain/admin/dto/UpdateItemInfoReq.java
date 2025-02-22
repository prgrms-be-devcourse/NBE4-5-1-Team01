package com.team1.beanstore.domain.admin.dto;

import com.team1.beanstore.domain.product.entity.ProductCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record UpdateItemInfoReq(
        @NonNull @NotBlank String name,
        @Min(1) int price,
        int inventory,
        @NonNull @NotBlank String description,
        @NonNull ProductCategory category
) {}