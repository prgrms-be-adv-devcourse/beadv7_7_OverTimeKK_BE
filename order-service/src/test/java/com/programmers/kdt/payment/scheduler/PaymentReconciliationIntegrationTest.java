package com.programmers.kdt.payment.scheduler;

import com.programmers.kdt.payment.client.pay.PaymentConfirmEvent;
import com.programmers.kdt.payment.client.pay.PaymentFailEvent;
import com.programmers.kdt.payment.client.pay.PaymentResultEventPublisher;
import com.programmers.kdt.payment.client.pg.MockPgClient;
import com.programmers.kdt.payment.client.pg.PgApproveResult;
import com.programmers.kdt.payment.entity.Payment;
import com.programmers.kdt.payment.entity.PaymentStatus;
import com.programmers.kdt.payment.repository.PaymentRepository;
import com.programmers.kdt.payment.service.PointService;
import com.programmers.kdt.payment.service.tx.PaymentTxOps;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DataJpaTest
public class PaymentReconciliationIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EntityManager em;

    private final MockPgClient mockPgClient = new MockPgClient();
    private final PaymentResultEventPublisher paymentResultEventPublisher = mock(PaymentResultEventPublisher.class);
    private final PointService pointService = mock(PointService.class);

    private PaymentReconciliationScheduler scheduler;

    @BeforeEach
    void setUp() {
        mockPgClient.reset();
        PaymentTxOps paymentTxOps = new PaymentTxOps(paymentRepository);
        scheduler = new PaymentReconciliationScheduler(
                paymentRepository, mockPgClient, paymentTxOps, paymentResultEventPublisher, pointService);
    }

    private Payment persistPendingPayment(String paymentKey) {
        Payment payment = Payment.create(1L, 10L, 10000L);
        payment.assignPaymentKey(paymentKey);
        payment.markPending();
        paymentRepository.saveAndFlush(payment);
        em.clear();
        return payment;
    }

    @Test
    @DisplayName("실제 DB 기준으로, PG 재조회 성공 시 PAID 확정, 승인 이벤트가 발행된다.")
    void reconcile_success_confirmPayment() {
        Payment pending = persistPendingPayment("PG_KEY_OK");
        mockPgClient.stubSelect("PG_KEY_OK", () -> new PgApproveResult(true, LocalDateTime.now()));

        scheduler.reconcilePayments();

        Payment result = paymentRepository.findById(pending.getId()).orElseThrow();
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
        verify(paymentResultEventPublisher).publishConfirmed(
                new PaymentConfirmEvent(result.getOrderId(), result.getId()));

    }

    @Test
    @DisplayName("실제 DB 기준으로, PG 재조회 실패 시 FAILED 확정, 실패 이벤트가 발행된다.")
    void reconcile_explicitFail_failPaymentAndRollback() {
        Payment pending = persistPendingPayment("PG_KEY_FAIL");
        mockPgClient.stubSelect("PG_KEY_FAIL", () -> new PgApproveResult(false, null));
        when(pointService.findUsedAmount(anyString())).thenReturn(3000L);

        scheduler.reconcilePayments();

        Payment result = paymentRepository.findById(pending.getId()).orElseThrow();
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);
        verify(pointService).rollbackPoint(anyString(), eq(3000L), anyString(), eq(true));
        verify(paymentResultEventPublisher).publishFailed(any(PaymentFailEvent.class));
    }

    @Test
    @DisplayName("PG가 계속 응답 없으면 AMBIGUOUS 상태와 수정시각 모두 그대로 유지된다.")
    void reconcile_stillAmbiguous_leavesRowUntouched() {
        Payment pending = persistPendingPayment("PG_KEY_TIMEOUT");
        LocalDateTime originModifiedAt = paymentRepository.findById(pending.getId()).orElseThrow().getModifiedAt();
        mockPgClient.stubSelect("PG_KEY_TIMEOUT", () -> {
            throw new RestClientException("simulated timeout");}
        );

        scheduler.reconcilePayments();

        Payment result = paymentRepository.findById(pending.getId()).orElseThrow();
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.CONFIRM_PENDING_VERIFICATION);
        assertThat(result.getModifiedAt()).isEqualTo(originModifiedAt);
        verifyNoInteractions(paymentResultEventPublisher);
    }
}

