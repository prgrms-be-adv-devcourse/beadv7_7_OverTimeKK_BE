package com.programmers.kdt.payment.dto;

public record ConfirmPaymentResponse(
        Long paymentId,
        String status
) {
}
