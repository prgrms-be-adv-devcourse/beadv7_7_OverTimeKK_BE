package com.programmers.kdt.order.service;

import com.programmers.kdt.common.exception.BusinessException;
import com.programmers.kdt.order.client.TicketClient;
import com.programmers.kdt.order.client.TicketHoldResult;
import com.programmers.kdt.order.dto.CreateOrderRequest;
import com.programmers.kdt.order.dto.CreateOrderResponse;
import com.programmers.kdt.order.entity.Order;
import com.programmers.kdt.order.entity.OrderItem;
import com.programmers.kdt.order.exception.OrderErrorCode;
import com.programmers.kdt.order.repository.OrderRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final TicketClient ticketClient;

    // 주문 요청
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        // 존재하는 좌석인지, 좌석 점유 여부 확인 및 점유 요청
       TicketHoldResult holdTicket = ticketClient.holdSeat(request.ticketId(), request.userId()) ; // 추후 ticketService

        // 주문 항목 생성 - 현재는 티켓 1매만 가능
        OrderItem item = OrderItem.create(holdTicket.ticketId(), holdTicket.ticketPrice());

        // 주문 생성 및 저장
        Order order = Order.create(request.userId(), List.of(item));
        Order savedOrder = orderRepository.save(order);

        return CreateOrderResponse.from(savedOrder);
    }

    // 주문 완료
    public void completeOrder(Long orderId){
        Order order = findOrder(orderId);
        order.complete();
    }

    // 주문 만료
    public void expiredOrder(Long orderId){
        Order order = findOrder(orderId);
        order.expire();
    }

    // 주문 취소
    @Transactional
    public void cancelOrder(Long orderId) {
        // 1. 주문  조회
        Order order = findOrder(orderId);

        // 2. 결제 취소
        // paymentService.cancel(orderId);
        // 3. 주문 취소
        order.cancel();

        // 4. 5분 뒤, 좌석 점유 해제 예약
        // ticketServcie.scheduleRelease(ticketId); -> userId도 함께 보내줘야 되는지
    }

    private Order findOrder(Long orderId){
        return orderRepository.findById(orderId)
                .orElseThrow(()->
                        new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));
    }
}