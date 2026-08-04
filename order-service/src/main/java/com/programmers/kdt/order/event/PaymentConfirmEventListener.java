package com.programmers.kdt.order.event;

import com.programmers.kdt.order.service.OrderService;
import com.programmers.kdt.payment.client.pay.PaymentConfirmEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentConfirmEventListener {

    private final OrderService orderService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PaymentConfirmEvent event){
        orderService.completeOrder(event.orderId());
    }
}
