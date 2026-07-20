package com.programmers.kdt.payment.dto;

public record FailPaymentResponse(
        Long paymentId,
        String status
) {

}
