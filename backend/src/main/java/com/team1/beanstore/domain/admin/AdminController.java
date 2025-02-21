package com.team1.beanstore.domain.admin;

import com.team1.beanstore.domain.order.OrderResponse;
import com.team1.beanstore.domain.order.OrderResponseWithDetail;
import com.team1.beanstore.domain.order.entity.OrderStatus;
import com.team1.beanstore.domain.order.service.OrderService;
import com.team1.beanstore.global.dto.Empty;
import com.team1.beanstore.global.dto.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/GCcoffee/admin")
public class AdminController {
    private final OrderService orderService;


    @GetMapping("/orders")
    public RsData<Page<OrderResponseWithDetail>> getOrders(@RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "10") int pageSize,
                                                           @RequestParam(defaultValue = "") String keyword,
                                                           @RequestParam(defaultValue = "asc") String sort) {
        Page<OrderResponseWithDetail> orderResponsePage = orderService.getOrders(page, pageSize, keyword, sort);
        return new RsData<>("200-1",
                "주문 전체 조회가 완료되었습니다.",
                orderResponsePage);
    }


    @GetMapping("/order/{id}")
    public RsData<OrderResponseWithDetail> getOrder(@PathVariable long id) {
        OrderResponseWithDetail orderResponseWithDetail = orderService.getOrder(id);
        return new RsData<>("200-1",
                "주문 상세 조회가 완료되었습니다.",
                orderResponseWithDetail);
    }


    record AdminOrderModifyReqBody(OrderStatus orderStatus) {
    }

    @PatchMapping("/order/{id}")
    public RsData<OrderResponse> modifyOrder(@PathVariable long id, @RequestBody AdminOrderModifyReqBody reqBody) {
        OrderResponse orderResponse = orderService.modify(id, reqBody.orderStatus());
        return new RsData<>("200-1",
                "주문 수정이 완료되었습니다.",
                orderResponse);
    }

    @DeleteMapping("/order/{id}")
    public RsData<Empty> deleteOrder(@PathVariable long id) {
        orderService.delete(id);
        return new RsData<>("200-1",
                "주문 삭제가 완료되었습니다.");
    }
}
