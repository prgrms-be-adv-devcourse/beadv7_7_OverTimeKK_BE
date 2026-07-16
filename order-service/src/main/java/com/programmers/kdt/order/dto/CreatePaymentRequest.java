package com.programmers.kdt.order.dto;

public record CreatePaymentRequest(
        Long orderId,
        Long amount
) {
}
