package com.programmers.kdt.payment.scheduler;

import com.programmers.kdt.payment.client.point.EndedPerformanceClient;
import com.programmers.kdt.payment.client.point.EndedTicket;
import com.programmers.kdt.payment.dto.PointEarnTarget;
import com.programmers.kdt.payment.repository.PointEarnTargetRepository;
import com.programmers.kdt.payment.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEarnScheduler {

    private static final double EARN_RATE = 0.01; // 티켓 가격의 1% 포인트 지급

    private final EndedPerformanceClient endedPerformanceClient;
    private final PointEarnTargetRepository pointEarnTargetRepository;
    private final PointService pointService;

    @Scheduled(cron = "0 01 0 * * *")
    public void earnPointsForEndedPerformances() {
        LocalDate targetDate = LocalDate.now().minusDays(1); // 어제 종료된 공연 -> 다음날 지급

        List<EndedTicket> endedTickets = endedPerformanceClient.findEndedTickets(targetDate);
        if (endedTickets.isEmpty()) {
            log.info("{} 종료된 공연의 정산 대상 티켓 없음", targetDate);
            return;
        }

        List<Long> ticketIds = endedTickets.stream().map(EndedTicket::ticketId).toList();
        List<PointEarnTarget> targets = pointEarnTargetRepository.findEarnTargetsByTicketIds(ticketIds);

        int successCount = 0;
        for (PointEarnTarget target : targets) {
            try {
                long earnAmount = Math.round(target.ticketPrice() * EARN_RATE);
                if (earnAmount <= 0) {
                    continue;
                }
                pointService.earnPoint(target.userId(), earnAmount, pointEarnEventId(target.ticketId()));
                successCount++;
            } catch (Exception e) {
                log.error("포인트 적립 실패 - ticketId={}, userId={}", target.ticketId(), target.userId(), e);
            }
        }
        log.info("{} 공연 종료 포인트 적립 대상 {}건 중 {}건 처리 완료", targetDate, targets.size(), successCount);
    }

    private String pointEarnEventId(Long ticketId) {
        return "TICKET:" + ticketId + ":POINT_EARN";
    }
}
