package com.programmers.kdt.standby.service;

import com.programmers.kdt.common.exception.BusinessException;
import com.programmers.kdt.performance.entity.PerformanceSession;
import com.programmers.kdt.performance.entity.PerformanceSessionId;
import com.programmers.kdt.performance.exception.PerformanceErrorCode;
import com.programmers.kdt.performance.repository.PerformanceSeatPriceRepository;
import com.programmers.kdt.performance.repository.PerformanceSessionRepository;
import com.programmers.kdt.standby.entity.Standby;
import com.programmers.kdt.standby.exception.StandbyErrorCode;
import com.programmers.kdt.standby.repository.StandbyRepository;
import com.programmers.kdt.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StandbyService {

    private final StandbyRepository standbyRepository;
    private final PerformanceSessionRepository performanceSessionRepository;
    private final PerformanceSeatPriceRepository performanceSeatPriceRepository;
    private final TicketRepository ticketRepository;

    public Long applyStandby(Long userId, Long performanceId, Long sessionNum, List<String> zones) {
        PerformanceSession session = performanceSessionRepository
                .findById(new PerformanceSessionId(sessionNum, performanceId))
                .orElseThrow(() -> new BusinessException(PerformanceErrorCode.PERFORMANCE_SESSION_NOT_FOUND));

        if (standbyRepository.existsByUserIdAndPerformanceSession(userId, session)) {
            throw new BusinessException(StandbyErrorCode.ALREADY_APPLIED);
        }

        validateZonesUsedByPerformance(performanceId, zones);
        validateAllZonesSoldOut(performanceId, sessionNum, zones);

        Standby standby = Standby.apply(userId, session, zones);
        return standbyRepository.save(standby).getStandbyId();
    }


    private void validateZonesUsedByPerformance(Long performanceId, List<String> zones) {
        List<String> usableZones = performanceSeatPriceRepository
                .findZonesByPerformance_PerformanceId(performanceId);
        if (!usableZones.containsAll(zones)) {
            throw new BusinessException(StandbyErrorCode.INVALID_ZONE);
        }
    }

    // 요청 zone이 모두 매진 상태여야 대기 신청 가능.
    private void validateAllZonesSoldOut(Long performanceId, Long sessionNum, List<String> zones) {
        List<String> availableZones = ticketRepository
                .findAvailableZones(performanceId, sessionNum, zones);
        if (!availableZones.isEmpty()) {
            throw new BusinessException(StandbyErrorCode.ZONE_NOT_SOLD_OUT);
        }
    }

    //대기매칭 성공

    //대기매칭 취소

    //부분대기 취소

    //매칭결제준비
}
