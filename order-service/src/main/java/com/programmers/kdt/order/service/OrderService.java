package com.programmers.kdt.order.service;

import com.programmers.kdt.common.exception.BusinessException;
import com.programmers.kdt.order.client.TicketClient;
import com.programmers.kdt.order.client.TicketHoldResult;
import com.programmers.kdt.order.client.TicketInfo;
import com.programmers.kdt.order.dto.*;
import com.programmers.kdt.order.entity.Order;
import com.programmers.kdt.order.entity.OrderItem;
import com.programmers.kdt.order.entity.OrderStatus;
import com.programmers.kdt.order.exception.OrderErrorCode;
import com.programmers.kdt.order.repository.OrderRepository;
import com.programmers.kdt.payment.dto.CancelPaymentRequest;
import com.programmers.kdt.payment.service.PaymentService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final TicketClient ticketClient;
    private final PaymentService paymentService;

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
    public CancelOrderResponse cancelOrder(Long orderId, CancelOrderRequest request) {
        Order order = findOrder(orderId);

        // 취소 가능한 주문인지 검증
        order.validateCancel();

        // 결제 취소
        paymentService.cancel(
                order.getOrderId(),
                new CancelPaymentRequest(request.reason())); // paymentService의 cancel메서드 파라미터 :orderId로 변경

        // 결제 취소 성공 -> 주문 취소 완료
        order.cancel();

        // 좌석 점유 해제 요청
        ticketClient.releaseSeat(order.getTicketId(), order.getUserId());

        return CancelOrderResponse.from(order);
    }

    // 주문 내역 조회
    @Transactional(readOnly = true)
    public List<GetOrderHistoryResponse> getOrderHistory(Long userId){
        List<Order> orderHistory = orderRepository.findByUserId(userId);

        // 공연장 -> ticketId 조회해서 가져오기
        return orderHistory.stream()
                .map(order -> {
                    Long ticketId = order.getTicketId();
                    TicketInfo ticketInfo = ticketClient.getTicket(ticketId);
                    return GetOrderHistoryResponse.from(order, ticketInfo);
                })
                .toList();
    }

    private Order findOrder(Long orderId){
        return orderRepository.findById(orderId)
                .orElseThrow(()->
                        new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));
    }

}