package com.programmers.kdt.payment.service;


import com.programmers.kdt.common.exception.BusinessException;
import com.programmers.kdt.order.entity.Order;
import com.programmers.kdt.order.repository.OrderRepository;
import com.programmers.kdt.payment.client.PgClient;
import com.programmers.kdt.payment.client.PgReadyResult;
import com.programmers.kdt.payment.dto.CreatePaymentRequest;
import com.programmers.kdt.payment.dto.CreatePaymentResponse;
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

    @Mock private PaymentRepository paymentRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private PaymentRefundRepository paymentRefundRepository;
    @Mock private PgClient pgClient;

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


}