package com.programmers.kdt.ticket.service;

import com.programmers.kdt.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public void holdSeat(Long ticketId, Long userId) {
        // TODO
    }

    public void releaseHold(Long ticketId) {
        // TODO
    }
}
