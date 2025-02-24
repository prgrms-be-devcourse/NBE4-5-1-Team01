package com.team1.beanstore.domain.product.entity;

import com.team1.beanstore.domain.admin.dto.UpdateItemInfoReq;
import com.team1.beanstore.global.exception.ServiceException;
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

    public void updateInfo(UpdateItemInfoReq reqBody) {
        this.name = reqBody.name();
        this.price = reqBody.price();
        this.inventory = reqBody.inventory();
        this.description = reqBody.description();
        this.imageUrl = reqBody.imageUrl();
        this.category = reqBody.category();
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void decreaseInventory(int quantity) {
        if (this.inventory < quantity) {
            throw new ServiceException("404-1","재고가 부족합니다.");
        }
        this.inventory -= quantity;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}