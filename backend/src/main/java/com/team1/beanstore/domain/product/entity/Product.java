package com.team1.beanstore.domain.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "products")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private Integer inventory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Version
    private Long version;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void updateProduct(
            String name,
            int price,
            String imageUrl,
            int inventory,
            String description,
            ProductCategory category
    ) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.inventory = inventory;
        this.description = description;
        this.category = category;
    }

    public void decreaseInventory(int quantity) {
        if (this.inventory < quantity) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.inventory -= quantity;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}