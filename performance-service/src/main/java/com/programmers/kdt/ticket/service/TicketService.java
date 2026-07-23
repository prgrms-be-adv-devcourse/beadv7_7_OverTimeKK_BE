package com.programmers.kdt.ticket.service;

import com.programmers.kdt.standby.entity.StandbyStatus;
import com.programmers.kdt.standby.service.StandbyService;
import com.programmers.kdt.ticket.dto.CreateStandbyResponse;
import com.programmers.kdt.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final StandbyService standbyService;

    public void holdSeat(Long ticketId, Long userId) {
        // TODO
    }

    public void releaseHold(Long ticketId) {
        // TODO
    }

    public CreateStandbyResponse issueStandby(Long userId, Long performanceId, Long sessionNum, List<String> zones) {
        Long standbyId = standbyService.applyStandby(userId, performanceId, sessionNum, zones);
        return new CreateStandbyResponse(standbyId, zones, StandbyStatus.WAITING.name());
    }
}
