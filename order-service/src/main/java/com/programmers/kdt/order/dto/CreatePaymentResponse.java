package com.programmers.kdt.order.dto;

public record CreatePaymentResponse(
        Long paymentId,
        String status
) {
}
