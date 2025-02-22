package com.team1.beanstore.domain.admin;

import com.team1.beanstore.domain.order.OrderResponse;
import com.team1.beanstore.domain.order.OrderResponseWithDetail;
import com.team1.beanstore.domain.order.entity.OrderStatus;
import com.team1.beanstore.domain.order.service.OrderService;
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
public class AdminOrderController {
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


}
