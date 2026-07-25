package com.programmers.kdt.standby.repository;

import com.programmers.kdt.performance.entity.PerformanceSession;
import com.programmers.kdt.standby.entity.Standby;
import com.programmers.kdt.standby.entity.StandbyStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface StandbyRepository extends JpaRepository<Standby, Long> {
    List<Standby> findByPerformanceSessionOrderByReservedAtAsc(PerformanceSession session);

    boolean existsByUserIdAndPerformanceSession(Long userId, PerformanceSession performanceSession);

    // zone을 지망(zone1/zone2/zone3)에 담아 대기 중인 사람을 신청 순서(FIFO)대로 조회. 매칭 대상 선정용.
    // pageable로 조회 건수를 제한한다 (예: PageRequest.of(0, 1) -> FIFO 선두 1명만 DB에서 LIMIT).
    @Query("select s from Standby s " +
            "where s.performanceSession = :session " +
            "and s.standbyStatus = :status " +
            "and (:zone = s.zone1 or :zone = s.zone2 or :zone = s.zone3) " +
            "order by s.reservedAt asc")
    List<Standby> findMatchCandidates(@Param("session") PerformanceSession session,
                                       @Param("zone") String zone,
                                       @Param("status") StandbyStatus status,
                                       Pageable pageable);

    @Query("select count(s) from Standby s " +
            "where s.performanceSession = :session " +
            "and s.standbyStatus = :status " +
            "and (:zone = s.zone1 or :zone = s.zone2 or :zone = s.zone3) " +
            "and s.reservedAt < :reservedAt")
    long countRankCount(@Param("session") PerformanceSession session,
                        @Param("zone") String zone,
                        @Param("status") StandbyStatus status,
                        @Param("reservedAt") LocalDateTime reservedAt);

}
