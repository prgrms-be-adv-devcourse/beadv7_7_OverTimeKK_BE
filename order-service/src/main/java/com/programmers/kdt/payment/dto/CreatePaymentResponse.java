package com.programmers.kdt.payment.dto;

import com.programmers.kdt.payment.entity.PaymentStatus;

public record CreatePaymentResponse(
        Long paymentId,
        String status,
        String transactionKey,
        String redirectionUrl
) {
}
