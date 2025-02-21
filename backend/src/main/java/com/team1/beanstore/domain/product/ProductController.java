package com.team1.beanstore.domain.product;

import com.team1.beanstore.domain.product.entity.ProductCategory;
import com.team1.beanstore.global.dto.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
            throw new IllegalArgumentException("페이지 번호는 0 이상이어야 합니다.");
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
            throw new IllegalArgumentException("페이지 번호는 0 이상이어야 합니다.");
        }

        Page<ProductResponse> products = productService.searchProductsByName(keyword, page, pageSize, sort);
        return new RsData<>(
                "200-2",
                "검색 결과 반환",
                products);
    }


    @GetMapping("/admin/items")
    public Page<ProductResponse> getItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "name") SearchKeywordType keywordType,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "asc") String sort
    ) {
        return productService.getListedItems(page, pageSize, keywordType, keyword, sort);
    }

    @GetMapping("/admin/item/{id}")
    public ProductResponse getItem(@RequestParam Long id) {
        return productService.getItem(id);
    }


    record ItemReqBody(
            @NotBlank
            String name,
            @Min(0)
            int price,
            @NotBlank
            String imageUrl,
            int inventory,
            @NotBlank
            String description,
            @NotBlank
            String category
    ) {}

    @PostMapping("/admin/item")
    public ResponseEntity<Map<String, Long>> createItem(
            @RequestBody @Valid ItemReqBody reqBody
    ) {
        ProductCategory categoryEnum;
        try {
            categoryEnum = ProductCategory.from(reqBody.category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 카테고리 값입니다: " + reqBody.category);
        }

        Map<String, Long> response =  productService.createItem(reqBody.name, reqBody.price, reqBody.imageUrl, reqBody.inventory, reqBody.description, categoryEnum);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/admin/item/{id}")
    public Long modifyItem(
            @RequestParam Long id,
            @RequestBody String name,
            @RequestBody int price,
            @RequestBody String image_url,
            @RequestBody int inventory,
            @RequestBody String description,
            @RequestBody ProductCategory category
    ) {
        return productService.modifyItem(id, name, price, image_url, inventory, description, category);
    }


    @DeleteMapping("/admin/delete/{id}")
    public Long deleteItem(
            @RequestParam Long id
    ) {
        return productService.deleteItem(id);
    }
}