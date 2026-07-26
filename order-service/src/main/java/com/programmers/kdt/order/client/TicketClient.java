package com.programmers.kdt.order.client;


import com.programmers.kdt.order.dto.TicketHoldRequest;
import com.programmers.kdt.order.dto.TicketHoldResult;

public interface TicketClient {

    TicketHoldResult holdSeat(TicketHoldRequest ticketRequest);
    void releaseSeat(Long ticketId, Long userId);
    TicketInfo getTicket(Long ticketId);
}
