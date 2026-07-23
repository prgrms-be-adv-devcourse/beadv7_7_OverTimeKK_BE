package com.programmers.kdt.standby.entity;

import com.programmers.kdt.common.exception.BusinessException;
import com.programmers.kdt.performance.entity.PerformanceSession;
import com.programmers.kdt.standby.exception.StandbyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class StandbyTest {

    private final PerformanceSession session = mock(PerformanceSession.class);

    @Nested
    @DisplayName("대기 신청(apply)")
    class Apply {

        @Test
        @DisplayName("지망 zone 3개로 신청하면 WAITING 상태로 생성되고 각 지망이 순서대로 담긴다.")
        void applyWithThreeZones() {
            // given
            Long userId = 1L;
            List<String> zones = List.of("A", "B", "C");

            // when
            Standby standby = Standby.apply(userId, session, zones);

            // then
            assertThat(standby.getUserId()).isEqualTo(userId);
            assertThat(standby.getPerformanceSession()).isEqualTo(session);
            assertThat(standby.getZone1()).isEqualTo("A");
            assertThat(standby.getZone2()).isEqualTo("B");
            assertThat(standby.getZone3()).isEqualTo("C");
            assertThat(standby.getStandbyStatus()).isEqualTo(StandbyStatus.WAITING);
            assertThat(standby.getReservedAt()).isNotNull();
        }

        @Test
        @DisplayName("지망 zone 1개로 신청하면 zone1만 채워지고 나머지는 null이다.")
        void applyWithOneZone() {
            // when
            Standby standby = Standby.apply(1L, session, List.of("A"));

            // then (null이 아닌 다른값으로 프론트에게 전달예정)
            assertThat(standby.getZone1()).isEqualTo("A");
            assertThat(standby.getZone2()).isNull();
            assertThat(standby.getZone3()).isNull();
            assertThat(standby.getStandbyStatus()).isEqualTo(StandbyStatus.WAITING);
        }

        @Test
        @DisplayName("지망 zone 2개로 신청하면 zone3만 null이다.")
        void applyWithTwoZones() {
            // when
            Standby standby = Standby.apply(1L, session, List.of("A", "B"));

            // then
            assertThat(standby.getZone1()).isEqualTo("A");
            assertThat(standby.getZone2()).isEqualTo("B");
            assertThat(standby.getZone3()).isNull();
        }

        @Test
        @DisplayName("지망 zone 목록이 null이면 INVALID_ZONE_COUNT 예외가 발생한다.")
        void applyWithNullZones() {
            assertThatThrownBy(() -> Standby.apply(1L, session, null))
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(StandbyErrorCode.INVALID_ZONE_COUNT)
                    );
        }

        @Test
        @DisplayName("지망 zone 목록이 비어있으면 INVALID_ZONE_COUNT 예외가 발생한다.")
        void applyWithEmptyZones() {
            assertThatThrownBy(() -> Standby.apply(1L, session, List.of()))
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(StandbyErrorCode.INVALID_ZONE_COUNT)
                    );
        }

        @Test
        @DisplayName("지망 zone이 3개를 초과하면 INVALID_ZONE_COUNT 예외가 발생한다.")
        void applyWithTooManyZones() {
            assertThatThrownBy(() -> Standby.apply(1L, session, List.of("A", "B", "C", "D")))
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(StandbyErrorCode.INVALID_ZONE_COUNT)
                    );
        }

        @Test
        @DisplayName("중복된 지망 zone이 있으면 DUPLICATE_ZONE 예외가 발생한다.")
        void applyWithDuplicateZones() {
            assertThatThrownBy(() -> Standby.apply(1L, session, List.of("A", "A", "B")))
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(StandbyErrorCode.DUPLICATE_ZONE)
                    );
        }

        @Test
        @DisplayName("지망 zone 목록에 null 항목이 포함되면 DUPLICATE_ZONE 예외가 발생한다.")
        void applyWithNullZoneElement() {
            // 현재 validateZones는 null 항목을 distinct 후 개수 불일치로 걸러낸다.
            assertThatThrownBy(() -> Standby.apply(1L, session, Arrays.asList("A", null)))
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(StandbyErrorCode.DUPLICATE_ZONE)
                    );
        }
    }

    @Nested
    @DisplayName("매칭 확정(hold)")
    class Hold {

        @Test
        @DisplayName("매칭된 zone이 zone2 지망이면 slot이 ZONE2로, 상태는 HELD로 바뀐다.")
        void holdSetsSlotAndStatus() {
            // given
            Standby standby = Standby.apply(1L, session, List.of("A", "B", "C"));

            // when
            standby.hold("B");

            // then
            assertThat(standby.getSlot()).isEqualTo(Slot.ZONE2);
            assertThat(standby.getStandbyStatus()).isEqualTo(StandbyStatus.HELD);
            assertThat(standby.getHeldAt()).isNotNull();
        }

        @Test
        @DisplayName("매칭하려는 zone이 지망 목록(zone1/zone2/zone3)에 없으면 예외가 발생한다.")
        void holdWithZoneNotRequested() {
            // given
            Standby standby = Standby.apply(1L, session, List.of("A"));

            // when & then
            assertThatThrownBy(() -> standby.hold("Z"))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}
