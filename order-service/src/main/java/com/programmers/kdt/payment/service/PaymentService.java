package com.programmers.kdt.payment.service;

import com.programmers.kdt.common.exception.BusinessException;
import com.programmers.kdt.common.exception.CommonErrorCode;
import com.programmers.kdt.common.exception.ErrorCode;
import com.programmers.kdt.order.entity.Order;
import com.programmers.kdt.order.repository.OrderRepository;
import com.programmers.kdt.payment.client.PgApproveCommand;
import com.programmers.kdt.payment.client.PgClient;
import com.programmers.kdt.payment.client.PgReadyCommand;
import com.programmers.kdt.payment.client.PgReadyResult;
import com.programmers.kdt.payment.dto.ConfirmPaymentRequest;
import com.programmers.kdt.payment.dto.CreatePaymentRequest;
import com.programmers.kdt.payment.dto.CreatePaymentResponse;
import com.programmers.kdt.payment.entity.Payment;
import com.programmers.kdt.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PgClient pgClient;

    @Transactional
    public CreatePaymentResponse pay(CreatePaymentRequest request) {
        orderRepository.findById(request.orderId())
                .orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

        // 결제 생성
        Payment payment = Payment.create(request.orderId(), request.amount());

        // 나중에 PG사 요청은 트랜잭션에서 빼는 것을 고려
        PgReadyResult readyResult = pgClient.ready(new PgReadyCommand(request.orderId(), request.amount()));
        // PG사 키 할당
        payment.assignPaymentKey(readyResult.transactionKey());
        paymentRepository.save(payment);

        return new CreatePaymentResponse(
                payment.getId(),
                payment.getPaymentStatus().name(),
                readyResult.transactionKey(),
                readyResult.redirectionUrl()
        );
    }



    public void cancelPayment(Long paymentId) {
        // TODO
    }
}
