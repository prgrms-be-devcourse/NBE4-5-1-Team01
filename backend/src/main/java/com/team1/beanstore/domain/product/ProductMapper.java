package com.team1.beanstore.domain.product;

import com.team1.beanstore.domain.admin.dto.CreateItemReq;
import com.team1.beanstore.domain.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(CreateItemReq createItemReq) {
        return Product.builder()
                .name(createItemReq.name())
                .price(createItemReq.price())
                .imageUrl(createItemReq.imageUrl())
                .inventory(createItemReq.inventory())
                .description(createItemReq.description())
                .category(createItemReq.category())
                .build();
    }
}
