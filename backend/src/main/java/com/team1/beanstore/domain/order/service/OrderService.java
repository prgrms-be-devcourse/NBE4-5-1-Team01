package com.team1.beanstore.domain.order.service;

import com.team1.beanstore.domain.order.OrderMapper;
import com.team1.beanstore.domain.order.OrderResponse;
import com.team1.beanstore.domain.order.OrderResponseWithDetail;
import com.team1.beanstore.domain.order.entity.Order;
import com.team1.beanstore.domain.order.entity.OrderItem;
import com.team1.beanstore.domain.order.entity.OrderStatus;
import com.team1.beanstore.domain.order.repository.OrderRepository;
import com.team1.beanstore.global.dto.PageDto;
import com.team1.beanstore.global.exception.ServiceException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderItemService orderItemService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(String email, String address, String zipCode, Map<Long, Integer> productQuantities) {
        try {
            Order order = orderMapper.toOrder(email, address, zipCode);
            List<OrderItem> orderItems = orderItemService.createOrderItems(order, productQuantities);
            order.addOrderItems(orderItems);
            orderRepository.save(order);
            return OrderResponse.from(order);
        } catch (OptimisticLockException e) {
            throw new ServiceException("409-1", "동시 주문이 많아 처리가 실패했습니다. 다시 시도해주세요.");
        }
    }

    public PageDto<OrderResponseWithDetail> getOrders(int page, int pageSize, String keyword, String sort) {
        Pageable pageable = PageRequest.of(page - 1, pageSize,
                Sort.by(sort.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, "id"));
        String likeKeyword = "%" + keyword + "%";

        Page<OrderResponseWithDetail> mappedOrders = orderRepository.findByEmailLike(likeKeyword, pageable).map(OrderResponseWithDetail::new);
        return new PageDto<>(mappedOrders);
    }

    public OrderResponseWithDetail getOrder(long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new ServiceException("404-1", "존재하지 않는 주문입니다."));

        return new OrderResponseWithDetail(order);
    }

    @Transactional
    public OrderResponse modify(long id, OrderStatus orderStatus) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new ServiceException("404-1", "존재하지 않는 주문입니다."));

        order.setOrderStatus(orderStatus);
        return OrderResponse.from(order);
    }

    public void delete(long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new ServiceException("404-1", "존재하지 않는 주문입니다."));

        orderRepository.delete(order);
    }

    public long count() {
        return orderRepository.count();
    }
}