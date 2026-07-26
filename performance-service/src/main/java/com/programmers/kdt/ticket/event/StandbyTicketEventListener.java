package com.programmers.kdt.ticket.event;

import com.programmers.kdt.common.exception.BusinessException;
import com.programmers.kdt.standby.event.StandbyTicketEvent;
import com.programmers.kdt.ticket.entity.Ticket;
import com.programmers.kdt.ticket.exception.TicketErrorCode;
import com.programmers.kdt.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class StandbyTicketEventListener {

    private final TicketRepository ticketRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onStandbyTicket(StandbyTicketEvent event) {
        Ticket ticket = ticketRepository.findById(event.ticketId())
                .orElseThrow(() -> new BusinessException(TicketErrorCode.TICKET_NOT_FOUND, event.ticketId()));
        ticket.standbyTicket(event.standbyUserId(), event.standbyExpiredAt());
    }
}
