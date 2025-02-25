package com.team1.beanstore.domain.admin;

import com.team1.beanstore.domain.order.dto.OrderResponse;
import com.team1.beanstore.domain.order.dto.OrderResponseWithDetail;
import com.team1.beanstore.domain.order.dto.OrderResponseWithItems;
import com.team1.beanstore.domain.order.entity.OrderStatus;
import com.team1.beanstore.domain.order.service.OrderService;
import com.team1.beanstore.global.dto.Empty;
import com.team1.beanstore.global.dto.PageDto;
import com.team1.beanstore.global.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/GCcoffee/admin/orders")
@Tag(name = "관리자 주문 API", description = "관리자가 주문을 관리하는 API입니다.")
public class AdminOrderController {
    private final OrderService orderService;


    @Operation(summary = "주문 목록 조회", description = "모든 주문 목록을 페이징하여 조회합니다.")
    @GetMapping()
    public RsData<PageDto<OrderResponseWithDetail>> getOrders(@RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "10") int pageSize,
                                                              @RequestParam(defaultValue = "") String keyword,
                                                              @RequestParam(defaultValue = "asc") String sort) {
        PageDto<OrderResponseWithDetail> orderResponsePage = orderService.getOrders(page, pageSize, keyword, sort);
        return new RsData<>("200-1",
                "주문 전체 조회가 완료되었습니다.",
                orderResponsePage);
    }

    @Operation(summary = "주문 상세 조회", description = "특정 주문의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public RsData<OrderResponseWithItems> getOrder(@PathVariable long id) {
        OrderResponseWithItems orderResponseWithItems = orderService.getOrder(id);
        return new RsData<>("200-1",
                "%d번 주문 상세 조회가 완료되었습니다.".formatted(id),
                orderResponseWithItems);
    }


    public record AdminOrderModifyReqBody(OrderStatus orderStatus) {
    }

    @Operation(summary = "주문 수정", description = "주문의 상태를 변경합니다.")
    @PatchMapping("/{id}")
    public RsData<OrderResponse> modifyOrder(@PathVariable long id, @RequestBody AdminOrderModifyReqBody reqBody) {
        OrderResponse orderResponse = orderService.modify(id, reqBody.orderStatus());
        return new RsData<>("200-1",
                "%d번 주문 수정이 완료되었습니다.".formatted(id),
                orderResponse);
    }

    @Operation(summary = "주문 삭제", description = "특정 주문을 삭제합니다.")
    @DeleteMapping("/{id}")
    public RsData<Empty> deleteOrder(@PathVariable long id) {
        orderService.delete(id);
        return new RsData<>("200-1",
                "%d번 주문 삭제가 완료되었습니다.".formatted(id));
    }
}