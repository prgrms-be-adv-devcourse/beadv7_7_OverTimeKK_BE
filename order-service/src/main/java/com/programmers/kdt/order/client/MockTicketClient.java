package com.programmers.kdt.order.client;

import org.springframework.stereotype.Component;

@Component
public class MockTicketClient implements TicketClient {
    public TicketHoldResult holdSeat(Long ticketId, Long userId){
        return new TicketHoldResult(ticketId, 50_000L);
    }
}
