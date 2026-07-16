package com.programmers.kdt.performance.service;

import com.programmers.kdt.performance.dto.CreateStandbyResponse;
import com.programmers.kdt.performance.repository.StandbyRepository;
import com.programmers.kdt.performance.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final StandbyRepository standbyRepository;

    public void holdSeat(Long ticketId, Long userId) {
        // TODO
    }

    public void releaseHold(Long ticketId) {
        // TODO
    }

    public CreateStandbyResponse issueStandby(Long userId, Long sessionNum, String zone) {
        // TODO: (sessionNum, zone) 단위로 순번 채번
        return null;
    }
}
