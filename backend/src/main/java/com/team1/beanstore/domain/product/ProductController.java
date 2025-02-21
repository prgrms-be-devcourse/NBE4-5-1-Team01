package com.team1.beanstore.domain.product;

import com.team1.beanstore.domain.product.entity.ProductCategory;
import com.team1.beanstore.global.dto.RsData;
import com.team1.beanstore.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/GCcoffee")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/items")
    public RsData<Page<ProductResponse>> getProductsByCategory(
            @RequestParam ProductCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "asc") String sort) {

        if (page < 0) {
            throw new ServiceException("400-3", "페이지 번호는 0 이상이어야 합니다.");
        }

        Page<ProductResponse> products = productService.getProductsByCategory(category, page, pageSize, sort);
        return new RsData<>(
                "200-1",
                "상품 조회 성공",
                products);
    }

    @GetMapping("/search")
    public RsData<Page<ProductResponse>> searchProductsByName(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "asc") String sort) {

        if (page < 0) {
            throw new ServiceException("400-3", "페이지 번호는 0 이상이어야 합니다.");
        }

        Page<ProductResponse> products = productService.searchProductsByName(keyword, page, pageSize, sort);
        return new RsData<>(
                "200-2",
                "검색 결과 반환",
                products);
    }
}