package com.team1.beanstore.domain.admin;

import com.team1.beanstore.domain.admin.dto.CreateItemReq;
import com.team1.beanstore.domain.admin.dto.UpdateItemInfoReq;
import com.team1.beanstore.domain.product.ProductResponse;
import com.team1.beanstore.domain.product.ProductService;
import com.team1.beanstore.domain.product.SearchKeywordType;
import com.team1.beanstore.global.dto.PageDto;
import com.team1.beanstore.global.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/GCcoffee/admin")
@Tag(name = "관리자 상품 API", description = "관리자가 상품을 관리하는 API")
public class AdminProductController {

    private final ProductService productService;

    @Operation(summary = "상품 목록 조회", description = "등록된 상품 목록을 조회합니다.")
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

    @Operation(summary = "상품 상세 조회", description = "특정 상품의 상세 정보를 조회합니다.")
    @GetMapping("/item/{id}")
    public RsData<ProductResponse> getItem(@PathVariable Long id) {
        ProductResponse product = productService.getItem(id);
        return new RsData<>(
                "200-1",
                "상품 조회 성공",
                product);
    }

    @Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다.")
    @PostMapping(value = "/item")
//    @PostMapping(value = "/item", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RsData<Map<String, Long>> createItem(@RequestBody @Valid CreateItemReq reqBody) {
        Map<String, Long> response = productService.createItem(reqBody);
        return new RsData<>("201-1", "상품 등록 성공", response);
    }

    @Operation(summary = "상품 수정", description = "기존 상품의 정보를 수정합니다.")
    @PatchMapping("/item/{id}")
    public RsData<Map<String, Long>> modifyItem(@PathVariable Long id, @RequestBody @Valid UpdateItemInfoReq reqBody) {
        Map<String, Long> response = productService.modifyItem(id, reqBody);
        return new RsData<>(
                "200-1",
                "%s번 상품 수정 성공".formatted(id), response);
    }

    @Operation(summary = "상품 이미지 수정", description = "기존 상품의 이미지 URL을 수정합니다.")
    @PatchMapping("/item/{id}/image")
    public RsData<Map<String, String>> modifyItemImage(@PathVariable Long id, @RequestParam String imageUrl) {
        Map<String, String> response = productService.modifyItemImage(id, imageUrl);
        return new RsData<>(
                "200-2",
                "%s번 상품 이미지 수정 성공".formatted(id),
                response);
    }

    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    @DeleteMapping("/delete/{id}")
    public RsData<Map<String, Long>> deleteItem(@PathVariable Long id) {
        Map<String, Long> response =  productService.deleteItem(id);

        return new RsData<>(
                "200-1",
                "%d번 상품 삭제 성공".formatted(id),
                response);
    }
}