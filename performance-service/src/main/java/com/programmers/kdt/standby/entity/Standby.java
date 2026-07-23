package com.programmers.kdt.standby.entity;

import com.programmers.kdt.common.entity.BaseTimeEntity;
import com.programmers.kdt.common.exception.BusinessException;
import com.programmers.kdt.performance.entity.PerformanceSession;
import com.programmers.kdt.standby.exception.StandbyErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uk_standby_user_session",
        columnNames = {"user_id", "session_num", "performance_id"}))
public class Standby extends BaseTimeEntity {

    private static final int MAX_ZONE_COUNT = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long standbyId;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "session_num"),
            @JoinColumn(name = "performance_id")
    })
    private PerformanceSession performanceSession;

    private String zone1;

    private String zone2;

    private String zone3;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StandbyStatus standbyStatus;

    // 어느 zone 슬롯(zone1/zone2/zone3)으로 매칭됐는지. 이름(slot)은 임시 확정이며 팀 논의 예정
    @Enumerated(EnumType.STRING)
    private Slot slot;

    // 판매자가 우선순위를 파악하기 위한 변수(1or2)
    private Long priority;

    // FIFO 기준이 되는 대기신청 시각
    private LocalDateTime reservedAt;
    // 결제 매칭 시각
    private LocalDateTime heldAt;

    public static Standby apply(Long userId, PerformanceSession session, List<String> zones){

        validateZones(zones);
        Standby standby = new Standby();
        standby.userId = userId;
        standby.performanceSession = session;
        standby.zone1 = zones.get(0);
        standby.zone2 = zones.size() > 1 ? zones.get(1) : null;
        standby.zone3 = zones.size() > 2 ? zones.get(2) : null;
        standby.standbyStatus = StandbyStatus.WAITING;
        standby.reservedAt = LocalDateTime.now();
        return standby;
    }


    private static void validateZones(List<String> zones) {
        if (zones == null || zones.isEmpty() || zones.size() > MAX_ZONE_COUNT) {
            throw new BusinessException(StandbyErrorCode.INVALID_ZONE_COUNT);
        }
        long distinct = zones.stream().filter(Objects::nonNull).distinct().count();
        if (distinct != zones.size()) {
            throw new BusinessException(StandbyErrorCode.DUPLICATE_ZONE);
        }
    }
}
