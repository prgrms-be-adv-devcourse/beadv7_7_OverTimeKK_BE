package com.programmers.kdt.payment.dto;

import java.time.LocalDateTime;

public record PaymentHistoryResponse(
        Long paymentId,
        Long orderId,
        Long amount,
        String status,
        LocalDateTime createdAt
) {
}
