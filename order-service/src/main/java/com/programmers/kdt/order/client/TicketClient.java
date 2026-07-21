package com.programmers.kdt.order.client;

public interface TicketClient {

    TicketHoldResult holdSeat(Long ticketId, Long userId);
}
