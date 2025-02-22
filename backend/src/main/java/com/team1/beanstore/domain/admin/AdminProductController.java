package com.team1.beanstore.domain.admin;

import com.team1.beanstore.domain.order.service.OrderService;
import com.team1.beanstore.domain.product.ProductResponse;
import com.team1.beanstore.domain.product.ProductService;
import com.team1.beanstore.domain.product.SearchKeywordType;
import com.team1.beanstore.domain.product.entity.ProductCategory;
import com.team1.beanstore.global.dto.PageDto;
import com.team1.beanstore.global.dto.RsData;
import com.team1.beanstore.global.exception.ServiceException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/GCcoffee/admin")
public class AdminProductController {
    private final OrderService orderService;
    private final ProductService productService;

    @GetMapping("/items")
    public RsData<PageDto<ProductResponse>> getItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "name") SearchKeywordType keywordType,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "asc") String sort
    ) {

        PageDto<ProductResponse> products = productService.getListedItems(page, pageSize, keywordType, keyword, sort);
        return new RsData<>(
                "200-1",
                "상품 목록 조회 성공",
                products);
    }

    @GetMapping("/item/{id}")
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

    @PostMapping("/item")
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

    @PatchMapping("/item/{id}")
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


    @DeleteMapping("/delete/{id}")
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
