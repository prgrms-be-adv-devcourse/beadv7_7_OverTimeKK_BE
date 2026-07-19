package com.programmers.kdt.order.service;

import com.programmers.kdt.order.dto.CreateOrderRequest;
import com.programmers.kdt.order.dto.CreateOrderResponse;
import com.programmers.kdt.order.entity.Order;
import com.programmers.kdt.order.repository.OrderRepository;
import com.programmers.kdt.payment.entity.Payment;
import com.programmers.kdt.payment.service.PaymentService;
import com.programmers.kdt.ticket.entity.Ticket;
import com.programmers.kdt.ticket.service.TicketService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final TicketService ticketService;
    private final PaymentService paymentService;
    private final UserClient userClient;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        return null;
    }

    public void cancelOrder(Long orderId) {
        // TODO
    }
}