package com.team1.beanstore.domain.product;

import com.team1.beanstore.domain.product.entity.ProductCategory;
import com.team1.beanstore.global.dto.RsData;
import com.team1.beanstore.global.exception.ServiceException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/GCcoffee/items")
@RequiredArgsConstructor
@Tag(name = "상품 API", description = "상품 관련 API")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "카테고리별 상품 조회", description = "카테고리에 따라 상품 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "상품 조회 성공")
    public RsData<Page<ProductResponse>> getProductsByCategory(
            @RequestParam ProductCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "asc") String sort) {

        if (page < 0) {
            throw new ServiceException("400-1", "페이지 번호는 0 이상이어야 합니다.");
        }

        Page<ProductResponse> products = productService.getProductsByCategory(category, page, pageSize, sort);
        return new RsData<>(
                "200-1",
                "상품 조회 성공",
                products);
    }

    @GetMapping("/search")
    @Operation(summary = "상품 검색", description = "상품명을 기준으로 검색하여 결과를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "검색 결과 반환")
    public RsData<Page<ProductResponse>> searchProductsByName(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "asc") String sort) {

        if (page < 0) {
            throw new ServiceException("400-1", "페이지 번호는 0 이상이어야 합니다.");
        }

        Page<ProductResponse> products = productService.searchProductsByName(keyword, page, pageSize, sort);
        return new RsData<>(
                "200-2",
                "검색 결과 반환",
                products);
    }
}