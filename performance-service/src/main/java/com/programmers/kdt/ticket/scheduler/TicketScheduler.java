package com.programmers.kdt.ticket.scheduler;

import com.programmers.kdt.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class TicketScheduler {

    private final TicketService ticketService;

    @Scheduled(fixedDelay = 60, timeUnit = TimeUnit.SECONDS)
    public void releaseHoldTicket() {
        ticketService.releaseExpiredHoldTickets();
    }
}
