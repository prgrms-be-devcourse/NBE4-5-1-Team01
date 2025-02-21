package com.team1.beanstore.domain.product;

import com.team1.beanstore.domain.product.entity.Product;
import com.team1.beanstore.domain.product.entity.ProductCategory;
import com.team1.beanstore.global.dto.RsData;
import com.team1.beanstore.global.exception.ServiceException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
            throw new ServiceException("400-1", "페이지 번호는 0 이상");
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
            throw new ServiceException("400-1", "페이지 번호는 0 이상");
        }

        Page<ProductResponse> products = productService.searchProductsByName(keyword, page, pageSize, sort);
        return new RsData<>(
                "200-2",
                "검색 결과 반환",
                products);
    }

    @GetMapping("/admin/items")
    public RsData<Page<ProductResponse>> getItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "name") SearchKeywordType keywordType,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "asc") String sort
    ) {

        Page<ProductResponse> products = productService.getListedItems(page, pageSize, keywordType, keyword, sort);
        return new RsData<>(
                "200-1",
                "상품 목록 조회 성공",
                products);
    }

    @GetMapping("/admin/item/{id}")
    public RsData<ProductResponse> getItem(@PathVariable Long id) {
        ProductResponse product = productService.getItem(id);
        return new RsData<>(
                "200-1",
                "상품 조회 성공",
                product);
    }


    record ItemReqBody(
            @NotBlank
            String name,
            @Min(1)
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
    public RsData<Map<String, Long>> createItem(
            @RequestBody @Valid ItemReqBody reqBody
    ) {
        ProductCategory categoryEnum;
        try {
            categoryEnum = ProductCategory.from(reqBody.category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException("400-1", "잘못된 카테고리 값입니다: " + reqBody.category);
        }

        Map<String, Long> response =  productService.createItem(reqBody.name, reqBody.price, reqBody.imageUrl, reqBody.inventory, reqBody.description, categoryEnum);



        return new RsData<>(
                "201-1",
                "상품 등록 성공",
                response);
    }

    @PatchMapping("/admin/item/{id}")
    public RsData<Map<String, Long>> modifyItem(
            @PathVariable Long id,
            @RequestBody ItemReqBody reqBody
    ) {
        ProductCategory categoryEnum;
        try {
            categoryEnum = ProductCategory.from(reqBody.category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException("400-1", "잘못된 카테고리 값입니다: " + reqBody.category);
        }

        Map<String, Long> response =  productService.modifyItem(id, reqBody.name, reqBody.price, reqBody.imageUrl, reqBody.inventory, reqBody.description, categoryEnum);
        return new RsData<>(
                "200-1",
                "%s번 상품 수정 성공".formatted(id),
                response);
    }


    @DeleteMapping("/admin/delete/{id}")
    public RsData<Map<String, Long>> deleteItem(
            @PathVariable Long id
    ) {
        Map<String, Long> response =  productService.deleteItem(id);

        return new RsData<>(
                "200-1",
                "%d번 상품 삭제 성공".formatted(id),
                response);
    }
}