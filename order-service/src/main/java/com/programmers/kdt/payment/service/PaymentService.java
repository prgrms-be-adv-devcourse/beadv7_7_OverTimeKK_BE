package com.programmers.kdt.payment.service;

import com.programmers.kdt.common.exception.BusinessException;
import com.programmers.kdt.common.exception.CommonErrorCode;
import com.programmers.kdt.order.entity.Order;
import com.programmers.kdt.order.repository.OrderRepository;
import com.programmers.kdt.payment.client.*;
import com.programmers.kdt.payment.dto.*;
import com.programmers.kdt.payment.entity.Payment;
import com.programmers.kdt.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PgClient pgClient;

    // 결제 생성
    @Transactional
    public CreatePaymentResponse pay(CreatePaymentRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

        // 결제 생성
        Payment payment = Payment.create(order.getOrderId(), order.getUserId(), request.amount());

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

    // 결제 확인
    @Transactional
    public ConfirmPaymentResponse confirm(Long paymentId, ConfirmPaymentRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

        // 요청한 결제와 다른 결제일 경우
        if (!payment.getPaymentKey().equals(request.transactionKey())) {
            throw new BusinessException(CommonErrorCode.BAD_REQUEST);
        }

        PgApproveResult approveResult = pgClient.approve(
                new PgApproveCommand(payment.getPaymentKey(), payment.getAmount()));

        // 결제 요청 성공 & 실패 분기
        if (approveResult.success()) {
            payment.approve();
        } else {
            payment.fail();
        }

        return new ConfirmPaymentResponse(payment.getId(), payment.getPaymentStatus().name());
    }

    // 결제 실패 요청
    @Transactional
    public FailPaymentResponse fail(Long paymentId, FailPaymentRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

        if (payment.getPaymentKey() != null) {
            pgClient.cancel(new PgCancelCommand(payment.getPaymentKey(), payment.getAmount(), request.reason()));
        }

        payment.fail();

        return new FailPaymentResponse(payment.getId(), payment.getPaymentStatus().name());
    }

    // 결제 내역 조회
    @Transactional(readOnly = true)
    public Page<PaymentHistoryResponse> getPaymentHistory(Long userId, Pageable pageable) {
        return paymentRepository.findByUserId(userId, pageable)
                .map(payment -> new PaymentHistoryResponse(
                        payment.getId(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentStatus().name(),
                        payment.getCreatedAt()
                ));
    }

    public void cancelPayment(Long paymentId) {

        // TODO
    }
}
