package com.programmers.kdt.order.client;


import com.programmers.kdt.order.dto.TicketHoldRequest;
import com.programmers.kdt.order.dto.TicketHoldResult;
import com.programmers.kdt.order.dto.TicketReleaseRequest;

public interface TicketClient {

    TicketHoldResult holdSeat(TicketHoldRequest ticketRequest);
    void releaseSeat(TicketReleaseRequest request);
    TicketInfo getTicket(Long ticketId);
}
