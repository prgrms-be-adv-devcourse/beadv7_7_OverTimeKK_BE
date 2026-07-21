package com.programmers.kdt.payment.client.refund;

import java.time.LocalDateTime;

public record RefundRequestEvent(
        Long paymentId,
        Long amount, // 취소 요청 금액
        String reason,
        LocalDateTime requestAt
) {
}
