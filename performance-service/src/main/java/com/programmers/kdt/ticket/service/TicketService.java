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

    /**
     * 대기 신청. 회차/구역 검증 및 순번(대기신청 시각) 저장은
     * {@link StandbyService#applyStandby}에 위임한다.
     */
    public CreateStandbyResponse issueStandby(Long userId, Long performanceId, Long sessionNum, List<String> zones) {
        Long standbyId = standbyService.applyStandby(userId, performanceId, sessionNum, zones);
        return new CreateStandbyResponse(standbyId, zones, StandbyStatus.WAITING.name());
    }
}
