package com.programmers.kdt.order.controller;

import com.programmers.kdt.common.response.ApiResponse;
import com.programmers.kdt.order.dto.*;
import com.programmers.kdt.order.service.OrderServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderServiceImpl;

    // 주문 요청
    @PostMapping
    public ResponseEntity<ApiResponse<CreateOrderResponse>> create(@Valid @RequestBody CreateOrderRequest request) {
        CreateOrderResponse response = orderServiceImpl.createOrder(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    // 주문 취소
    @PostMapping("/{orderId}/cancel")
    public ApiResponse<CancelOrderResponse> cancel(
            @PathVariable Long orderId,
            @RequestBody CancelOrderRequest request) {
        CancelOrderResponse response = orderServiceImpl.cancelOrder(orderId, request);
        return ApiResponse.success(response);
    }

    // 주문 내역 조회
    @GetMapping
    public ApiResponse<List<GetOrderHistoryResponse>> getOrderHistory(@RequestParam Long userId){
         List<GetOrderHistoryResponse> response = orderServiceImpl.getOrderHistory(userId);
         return ApiResponse.success(response);
    }
}
