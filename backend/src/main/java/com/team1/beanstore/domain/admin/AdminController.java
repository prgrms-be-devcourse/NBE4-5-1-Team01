package com.team1.beanstore.domain.admin;

import com.team1.beanstore.domain.order.dto.OrderResponse;
import com.team1.beanstore.domain.order.OrderResponseWithDetail;
import com.team1.beanstore.domain.order.entity.OrderStatus;
import com.team1.beanstore.domain.order.service.OrderService;
import com.team1.beanstore.global.dto.Empty;
import com.team1.beanstore.global.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/GCcoffee/admin")
@Tag(name = "관리자 주문 API", description = "관리자가 주문을 관리하는 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final OrderService orderService;

    @GetMapping("/orders")
    @Operation(summary = "주문 전체 조회", description = "관리자가 모든 주문을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "주문 조회 성공")
    public RsData<Page<OrderResponseWithDetail>> getOrders(@RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "10") int pageSize,
                                                           @RequestParam(defaultValue = "") String keyword,
                                                           @RequestParam(defaultValue = "asc") String sort) {
        Page<OrderResponseWithDetail> orderResponsePage = orderService.getOrders(page, pageSize, keyword, sort);
        return new RsData<>(
                "200-1",
                "주문 전체 조회가 완료되었습니다.",
                orderResponsePage);
    }


    @GetMapping("/order/{id}")
    @Operation(summary = "주문 상세 조회", description = "관리자가 특정 주문을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "주문 상세 조회 성공")
    public RsData<OrderResponseWithDetail> getOrder(@PathVariable long id) {
        OrderResponseWithDetail orderResponseWithDetail = orderService.getOrder(id);
        return new RsData<>(
                "200-1",
                "주문 상세 조회가 완료되었습니다.",
                orderResponseWithDetail);
    }


    public record AdminOrderModifyReqBody(OrderStatus orderStatus) {
    }

    @PatchMapping("/order/{id}")
    @Operation(summary = "주문 수정", description = "관리자가 주문 상태를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "주문 수정 완료")
    public RsData<OrderResponse> modifyOrder(@PathVariable long id, @RequestBody AdminOrderModifyReqBody reqBody) {
        OrderResponse orderResponse = orderService.modify(id, reqBody.orderStatus());
        return new RsData<>(
                "200-1",
                "주문 수정이 완료되었습니다.",
                orderResponse);
    }

    @DeleteMapping("/order/{id}")
    @Operation(summary = "주문 삭제", description = "관리자가 특정 주문을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "주문 삭제 완료")
    public RsData<Empty> deleteOrder(@PathVariable long id) {
        orderService.delete(id);
        return new RsData<>(
                "200-1",
                "주문 삭제가 완료되었습니다.");
    }
}