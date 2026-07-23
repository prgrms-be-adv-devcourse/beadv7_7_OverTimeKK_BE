package com.programmers.kdt.ticket.repository;

import com.programmers.kdt.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByPerformanceIdAndSessionNumAndZone(
            Long performanceId, Long sessionNum, String zone);

    // 요청 zone 중 아직 잔여 좌석(AVAILABLE)이 남아있는 zone 목록.
    // 비어 있으면 = 요청 zone 전부 매진 → 대기 신청 가능.
    @Query("select distinct t.zone from Ticket t " +
            "where t.performanceId = :performanceId and t.sessionNum = :sessionNum " +
            "and t.zone in :zones " +
            "and t.ticketStatus = com.programmers.kdt.ticket.entity.TicketStatus.AVAILABLE")
    List<String> findAvailableZones(@Param("performanceId") Long performanceId,
                                   @Param("sessionNum") Long sessionNum,
@Param("zones") List<String> zones);
        }
