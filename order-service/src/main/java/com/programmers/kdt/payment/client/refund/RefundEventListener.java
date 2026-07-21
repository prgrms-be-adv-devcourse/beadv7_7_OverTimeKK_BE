package com.programmers.kdt.payment.client.refund;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RefundEventListener {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRefund(RefundRequestEvent event) {
        // 실제 환불 처리 : performance-service 조회, 환불률 계산, PG 취소, PaymentRefund 저장
    }
}
