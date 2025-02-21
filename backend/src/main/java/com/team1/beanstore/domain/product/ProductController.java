package com.team1.beanstore.domain.product;

import com.team1.beanstore.domain.product.entity.ProductCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/GCcoffee")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/items")
    public Page<ProductResponse> getProductsByCategory(
            @RequestParam ProductCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "asc") String sort) {

        if (page < 0) {
            throw new IllegalArgumentException("페이지 번호는 0 이상이어야 합니다.");
        }

        return productService.getProductsByCategory(category, page, pageSize, sort);
    }

    @GetMapping("/search")
    public Page<ProductResponse> searchProductsByName(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "asc") String sort) {

        if (page < 0) {
            throw new IllegalArgumentException("페이지 번호는 0 이상이어야 합니다.");
        }

        return productService.searchProductsByName(keyword, page, pageSize, sort);
    }
}