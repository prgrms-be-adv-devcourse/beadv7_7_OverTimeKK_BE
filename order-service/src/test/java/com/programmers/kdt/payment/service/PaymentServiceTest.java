package com.programmers.kdt.payment.service;


import com.programmers.kdt.common.exception.BusinessException;
import com.programmers.kdt.order.entity.Order;
import com.programmers.kdt.order.repository.OrderRepository;
import com.programmers.kdt.payment.client.PgApproveCommand;
import com.programmers.kdt.payment.client.PgApproveResult;
import com.programmers.kdt.payment.client.PgClient;
import com.programmers.kdt.payment.client.PgReadyResult;
import com.programmers.kdt.payment.dto.ConfirmPaymentRequest;
import com.programmers.kdt.payment.dto.ConfirmPaymentResponse;
import com.programmers.kdt.payment.dto.CreatePaymentRequest;
import com.programmers.kdt.payment.dto.CreatePaymentResponse;
import com.programmers.kdt.payment.entity.Payment;
import com.programmers.kdt.payment.entity.PaymentStatus;
import com.programmers.kdt.payment.exception.PaymentErrorCode;
import com.programmers.kdt.payment.repository.PaymentRefundRepository;
import com.programmers.kdt.payment.repository.PaymentRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentRefundRepository paymentRefundRepository;
    @Mock
    private PgClient pgClient;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentRepository, orderRepository, paymentRefundRepository, pgClient);
    }

    @Nested
    @DisplayName("결제 생성")
    class Pay {

        private final CreatePaymentRequest request = new CreatePaymentRequest(1L, 10000L);

        @Test
        @DisplayName("정상 요청이면 결제가 생성된다.")
        void successPayment() {
            Order order = Mockito.mock(Order.class);
            when(order.getOrderId()).thenReturn(1L);
            when(order.getUserId()).thenReturn(1L);
            when(order.getTotalAmount()).thenReturn(10000L);

            PgReadyResult readyResult = mock(PgReadyResult.class);
            when(readyResult.transactionKey()).thenReturn("PG_KEY_123");

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(paymentRepository.existsByOrderId(1L)).thenReturn(false);
            when(pgClient.ready(any())).thenReturn(readyResult);

            CreatePaymentResponse response = paymentService.pay(request);

            assertThat(response).isNotNull();
            verify(paymentRepository).save(any());
        }

        @Test
        @DisplayName("주문이 존재하지 않으면 예외가 발생한다.")
        void payOrderNotFound() {
            when(orderRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> paymentService.pay(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(PaymentErrorCode.ORDER_NOT_FOUND);

            verifyNoInteractions(pgClient);
        }

        @Test
        @DisplayName("이미 결제가 생성된 주문이면 예외가 발생한다.")
        void payAlreadyExists() {
            Order order = Mockito.mock(Order.class);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(paymentRepository.existsByOrderId(1L)).thenReturn(true);

            assertThatThrownBy(() -> paymentService.pay(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(PaymentErrorCode.PAYMENT_ALREADY_EXISTS);

            verifyNoInteractions(pgClient);
        }

        @Test
        @DisplayName("요청 금액이 주문 금액과 다르면 예외가 발생한다.")
        void payAmountMismatch() {
            Order order = Mockito.mock(Order.class);
            when(order.getTotalAmount()).thenReturn(15000L);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(paymentRepository.existsByOrderId(1L)).thenReturn(false);

            assertThatThrownBy(() -> paymentService.pay(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(PaymentErrorCode.INVALID_PAYMENT_AMOUNT);

            verifyNoInteractions(pgClient);

        }

        @Test
        @DisplayName("PG사 요청이 실패하면 예외가 발생하고 저장되지 않는다.")
        void payPgFailure() {
            Order order = Mockito.mock(Order.class);
            when(order.getTotalAmount()).thenReturn(10000L);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(paymentRepository.existsByOrderId(1L)).thenReturn(false);
            when(pgClient.ready(any())).thenThrow(new BusinessException(PaymentErrorCode.PG_REQUEST_FAILED));

            assertThatThrownBy(() -> paymentService.pay(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(PaymentErrorCode.PG_REQUEST_FAILED);

            verify(paymentRepository, never()).save(any());

        }
    }

    @Nested
    @DisplayName("결제 승인")
    class Confirm {

        private Payment payment;

        @BeforeEach
        void setUp() {
            payment = Payment.create(1L, 100L, 10000L);
            payment.assignPaymentKey("PG_KEY_123");
        }

        @Test
        @DisplayName("PG 승인이 성공하면 결제 상태가 PAID로 바뀐다.")
        void confirmSuccess() {
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

            PgApproveResult approveResult = mock(PgApproveResult.class);
            when(approveResult.success()).thenReturn(true);
            when(pgClient.approve(any())).thenReturn(approveResult);

            ConfirmPaymentResponse response = paymentService.confirm(1L, new ConfirmPaymentRequest("PG_KEY_123"));

            assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
            assertThat(response).isNotNull();
        }

        @Test
        @DisplayName("PG 승인이 실패하면 결제 상태가 FAILED로 바뀐다.")
        void confirmFailure() {
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

            PgApproveResult approveResult = mock(PgApproveResult.class);
            when(approveResult.success()).thenReturn(false);
            when(pgClient.approve(any())).thenReturn(approveResult);

            paymentService.confirm(1L, new ConfirmPaymentRequest("PG_KEY_123"));

            assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);

        }

        @Test
        @DisplayName("결제를 찾을 수 없으면 예외가 발생한다.")
        void confirmPaymentNotFound() {
            when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> paymentService.confirm(1L, new ConfirmPaymentRequest("PG_KEY_123")))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(PaymentErrorCode.PAYMENT_NOT_FOUND);

            verifyNoInteractions(pgClient);

        }

        @Test
        @DisplayName("transactionKey가 일치하지 않으면 예외가 발생한다.")
        void confirmKeyMismatch() {
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
            assertThatThrownBy(() -> paymentService.confirm(1L, new ConfirmPaymentRequest("WRONG_KEY")))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(PaymentErrorCode.PAYMENT_KEY_MISMATCH);

            verifyNoInteractions(pgClient);
        }

        @Test
        @DisplayName("READY 상태가 아니면 예외가 발생하고 PG 승인 요청은 나가지 않는다.")
        void confirmInvalidStatus() {
            payment.approve();

            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

            assertThatThrownBy(() -> paymentService.confirm(1L, new ConfirmPaymentRequest("PG_KEY_123")))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(PaymentErrorCode.INVALID_PAYMENT_STATUS);

            verifyNoInteractions(pgClient);
        }

        @Test
        @DisplayName("PG 승인 요청이 실패하면 예외가 발생하고 상태는 변경되지 않는다.")
        void confirmPgRequestFailed() {
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
            when(pgClient.approve(any())).thenThrow(new BusinessException(PaymentErrorCode.PG_REQUEST_FAILED));

            assertThatThrownBy(() -> paymentService.confirm(1L, new ConfirmPaymentRequest("PG_KEY_123")))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(PaymentErrorCode.PG_REQUEST_FAILED);

            assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.READY);
        }

    }



}