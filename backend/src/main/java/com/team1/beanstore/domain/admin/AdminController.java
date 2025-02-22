package com.team1.beanstore.domain.admin;

import com.team1.beanstore.domain.order.OrderResponse;
import com.team1.beanstore.domain.order.OrderResponseWithDetail;
import com.team1.beanstore.domain.order.entity.OrderStatus;
import com.team1.beanstore.domain.order.service.OrderService;
import com.team1.beanstore.domain.product.ProductController;
import com.team1.beanstore.domain.product.ProductResponse;
import com.team1.beanstore.domain.product.ProductService;
import com.team1.beanstore.domain.product.SearchKeywordType;
import com.team1.beanstore.domain.product.entity.ProductCategory;
import com.team1.beanstore.global.dto.Empty;
import com.team1.beanstore.global.dto.PageDto;
import com.team1.beanstore.global.dto.RsData;
import com.team1.beanstore.global.exception.ServiceException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/GCcoffee/admin")
public class AdminController {
    private final OrderService orderService;
    private final ProductService productService;


    @GetMapping("/orders")
    @Transactional(readOnly = true)
    public RsData<PageDto<OrderResponseWithDetail>> getOrders(@RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "10") int pageSize,
                                                           @RequestParam(defaultValue = "") String keyword,
                                                           @RequestParam(defaultValue = "asc") String sort) {
        PageDto<OrderResponseWithDetail> orderResponsePage = orderService.getOrders(page, pageSize, keyword, sort);
        return new RsData<>("200-1",
                "주문 전체 조회가 완료되었습니다.",
                orderResponsePage);
    }


    @GetMapping("/orders/{id}")
    @Transactional(readOnly = true)
    public RsData<OrderResponseWithDetail> getOrder(@PathVariable long id) {
        OrderResponseWithDetail orderResponseWithDetail = orderService.getOrder(id);
        return new RsData<>("200-1",
                "주문 상세 조회가 완료되었습니다.",
                orderResponseWithDetail);
    }


    public record AdminOrderModifyReqBody(OrderStatus orderStatus) {
    }

    @PatchMapping("/orders/{id}")
    @Transactional
    public RsData<OrderResponse> modifyOrder(@PathVariable long id, @RequestBody AdminOrderModifyReqBody reqBody) {
        OrderResponse orderResponse = orderService.modify(id, reqBody.orderStatus());
        return new RsData<>("200-1",
                "주문 수정이 완료되었습니다.",
                orderResponse);
    }

    @DeleteMapping("/orders/{id}")
    @Transactional
    public RsData<Empty> deleteOrder(@PathVariable long id) {
        orderService.delete(id);
        return new RsData<>("200-1",
                "주문 삭제가 완료되었습니다.");
    }

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
