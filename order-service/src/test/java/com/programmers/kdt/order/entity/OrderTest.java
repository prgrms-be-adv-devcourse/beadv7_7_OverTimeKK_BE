package com.programmers.kdt.order.entity;

import com.programmers.kdt.common.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OrderTest {

    private static final Long userId = 1L;
    private static final Long ticketId = 10L;
    private static final Long orderAmount = 50_000L;

    @Nested
    @DisplayName("주문 생성")
    class CreateOrder{

        @Test
        @DisplayName("정상적인 정보로 주문을 생성하면 PENDING 상태로 초기화")
        void createOrder(){
            // when
            Order order = Order.create(userId, ticketId, orderAmount);

            // then
            assertThat(order.getUserId()).isEqualTo(userId);
            assertThat(order.getTicketId()).isEqualTo(ticketId);
            assertThat(order.getOrderAmount()).isEqualTo(orderAmount);
            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(order.getOrderDate()).isNotNull();
        }

        @Test
        @DisplayName("사용자 ID가 null이면 주문을 생성할 수 없다")
        void createOrderWithoutUserId() {
            assertThatThrownBy(() ->
                    Order.create(null, ticketId, orderAmount)
            ).isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("티켓 ID가 null이면 주문을 생성할 수 없다")
        void createOrderWithoutTicketId() {
            assertThatThrownBy(() ->
                    Order.create(userId, null, orderAmount)
            ).isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("주문 금액이 null이면 주문을 생성할 수 없다")
        void createOrderWithoutOrderAmount() {
            assertThatThrownBy(() ->
                    Order.create(userId, ticketId, null)
            ).isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("주문 금액이 0 이하이면 주문을 생성할 수 없다")
        void createOrderWithInvalidAmount() {
            assertThatThrownBy(() ->
                    Order.create(userId, ticketId, 0L)
            ).isInstanceOf(BusinessException.class);
        }
    }
}
