package com.programmers.kdt.payment.client.refund;

public interface RefundEventHandler { // 실제 로직 처리
    void handle(RefundRequestEvent event);
}
