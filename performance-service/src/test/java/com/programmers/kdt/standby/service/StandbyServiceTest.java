package com.programmers.kdt.standby.service;

import com.programmers.kdt.common.exception.BusinessException;
import com.programmers.kdt.performance.entity.PerformanceSession;
import com.programmers.kdt.performance.entity.PerformanceSessionId;
import com.programmers.kdt.performance.repository.PerformanceSeatPriceRepository;
import com.programmers.kdt.performance.repository.PerformanceSessionRepository;
import com.programmers.kdt.standby.entity.Standby;
import com.programmers.kdt.standby.exception.StandbyErrorCode;
import com.programmers.kdt.standby.repository.StandbyRepository;
import com.programmers.kdt.ticket.repository.TicketRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StandbyServiceTest {

    @Mock
    private StandbyRepository standbyRepository;

    @Mock
    private PerformanceSessionRepository performanceSessionRepository;

    @Mock
    private PerformanceSeatPriceRepository performanceSeatPriceRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private StandbyService standbyService;

    private static final Long USER_ID = 1L;
    private static final Long PERFORMANCE_ID = 10L;
    private static final Long SESSION_NUM = 2L;

    private final PerformanceSession session = mock(PerformanceSession.class);

    private PerformanceSessionId sessionId() {
        return new PerformanceSessionId(SESSION_NUM, PERFORMANCE_ID);
    }

    @Nested
    @DisplayName("대기 신청(applyStandby) - 회차 단위 1회 제한")
    class Apply {

        @Test
        @DisplayName("신청 내역이 없고 zone도 유효하면 대기 신청이 저장된다.")
        void applyWhenNotYetApplied() {
            // given
            given(performanceSessionRepository.findById(sessionId()))
                    .willReturn(Optional.of(session));
            given(standbyRepository.existsByUserIdAndPerformanceSession(USER_ID, session))
                    .willReturn(false);
            given(performanceSeatPriceRepository.findZonesByPerformance_PerformanceId(PERFORMANCE_ID))
                    .willReturn(List.of("A", "B", "C"));
            given(standbyRepository.save(any(Standby.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            standbyService.applyStandby(USER_ID, PERFORMANCE_ID, SESSION_NUM, List.of("A", "B", "C"));

            // then
            verify(standbyRepository).save(any(Standby.class));
        }

        @Test
        @DisplayName("이미 같은 회차에 신청했다면 ALREADY_APPLIED 예외가 발생하고 저장되지 않는다.")
        void rejectDuplicateApplyToSameSession() {
            // given
            given(performanceSessionRepository.findById(sessionId()))
                    .willReturn(Optional.of(session));
            given(standbyRepository.existsByUserIdAndPerformanceSession(USER_ID, session))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() ->
                    standbyService.applyStandby(USER_ID, PERFORMANCE_ID, SESSION_NUM, List.of("A", "B", "C")))
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(StandbyErrorCode.ALREADY_APPLIED)
                    );
            verify(standbyRepository, never()).save(any(Standby.class));
        }

        @Test
        @DisplayName("같은 회차라면 지망 zone이 달라도 이미 신청했으므로 차단된다.")
        void rejectDuplicateEvenWhenZonesDiffer() {
            // given - 최초 신청은 A/B/C , 전혀 다른 zone(D/E/F)으로 재신청
            given(performanceSessionRepository.findById(sessionId()))
                    .willReturn(Optional.of(session));
            given(standbyRepository.existsByUserIdAndPerformanceSession(USER_ID, session))
                    .willReturn(true);

            // when & then - zone이 달라도 (userId, session) 기준으로 이미 신청 → 차단
            assertThatThrownBy(() ->
                    standbyService.applyStandby(USER_ID, PERFORMANCE_ID, SESSION_NUM, List.of("D", "E", "F")))
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(StandbyErrorCode.ALREADY_APPLIED)
                    );
            verify(standbyRepository, never()).save(any(Standby.class));
        }
    }

    @Nested
    @DisplayName("대기 신청(applyStandby) - zone 유효성 검증")
    class ZoneValidation {

        @Test
        @DisplayName("요청 zone이 모두 해당 공연이 사용하는 zone이면 저장된다.")
        void applyWithZonesUsedByPerformance() {
            // given - 공연이 사용하는 zone은 A/B/C, 요청은 그 부분집합
            given(performanceSessionRepository.findById(sessionId()))
                    .willReturn(Optional.of(session));
            given(standbyRepository.existsByUserIdAndPerformanceSession(USER_ID, session))
                    .willReturn(false);
            given(performanceSeatPriceRepository.findZonesByPerformance_PerformanceId(PERFORMANCE_ID))
                    .willReturn(List.of("A", "B", "C"));
            given(standbyRepository.save(any(Standby.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            standbyService.applyStandby(USER_ID, PERFORMANCE_ID, SESSION_NUM, List.of("A", "B"));

            // then
            verify(standbyRepository).save(any(Standby.class));
        }

        @Test
        @DisplayName("요청 zone 중 하나라도 해당 공연이 쓰지 않는 값이면 INVALID_ZONE 예외가 발생하고 저장되지 않는다.")
        void rejectZoneNotUsedByPerformance() {
            // given - 공연이 사용하는 zone은 A/B/C 인데 요청에 없는 zone "Z"가 섞임
            given(performanceSessionRepository.findById(sessionId()))
                    .willReturn(Optional.of(session));
            given(standbyRepository.existsByUserIdAndPerformanceSession(USER_ID, session))
                    .willReturn(false);
            given(performanceSeatPriceRepository.findZonesByPerformance_PerformanceId(PERFORMANCE_ID))
                    .willReturn(List.of("A", "B", "C"));

            // when & then
            assertThatThrownBy(() ->
                    standbyService.applyStandby(USER_ID, PERFORMANCE_ID, SESSION_NUM, List.of("A", "Z")))
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(StandbyErrorCode.INVALID_ZONE)
                    );
            verify(standbyRepository, never()).save(any(Standby.class));
        }
    }

    @Nested
    @DisplayName("대기 신청(applyStandby) - 매진 전제 검증")
    class SoldOutPrecondition {

        @Test
        @DisplayName("요청 zone이 모두 매진이면(잔여 좌석 없음) 대기 신청이 저장된다.")
        void applyWhenAllZonesSoldOut() {
            // given - 유효 zone A/B/C, 잔여 좌석 있는 zone은 없음(빈 목록) → 전부 매진
            given(performanceSessionRepository.findById(sessionId()))
                    .willReturn(Optional.of(session));
            given(standbyRepository.existsByUserIdAndPerformanceSession(USER_ID, session))
                    .willReturn(false);
            given(performanceSeatPriceRepository.findZonesByPerformance_PerformanceId(PERFORMANCE_ID))
                    .willReturn(List.of("A", "B", "C"));
            given(ticketRepository.findAvailableZones(PERFORMANCE_ID, SESSION_NUM, List.of("A", "B")))
                    .willReturn(List.of());
            given(standbyRepository.save(any(Standby.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            standbyService.applyStandby(USER_ID, PERFORMANCE_ID, SESSION_NUM, List.of("A", "B"));

            // then
            verify(standbyRepository).save(any(Standby.class));
        }

        @Test
        @DisplayName("요청 zone 중 하나라도 잔여 좌석이 있으면 ZONE_NOT_SOLD_OUT 예외가 발생하고 저장되지 않는다.")
        void rejectWhenAnyZoneHasAvailableSeats() {
            // given - 유효 zone A/B/C, 그런데 A에는 아직 잔여 좌석이 있음
            given(performanceSessionRepository.findById(sessionId()))
                    .willReturn(Optional.of(session));
            given(standbyRepository.existsByUserIdAndPerformanceSession(USER_ID, session))
                    .willReturn(false);
            given(performanceSeatPriceRepository.findZonesByPerformance_PerformanceId(PERFORMANCE_ID))
                    .willReturn(List.of("A", "B", "C"));
            given(ticketRepository.findAvailableZones(PERFORMANCE_ID, SESSION_NUM, List.of("A", "B")))
                    .willReturn(List.of("A"));

            // when & then
            assertThatThrownBy(() ->
                    standbyService.applyStandby(USER_ID, PERFORMANCE_ID, SESSION_NUM, List.of("A", "B")))
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(StandbyErrorCode.ZONE_NOT_SOLD_OUT)
                    );
            verify(standbyRepository, never()).save(any(Standby.class));
        }
    }
}
