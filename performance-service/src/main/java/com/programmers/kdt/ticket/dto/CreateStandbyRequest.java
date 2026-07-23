package com.programmers.kdt.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 대기(Standby) 신청 요청.
 *
 * <p>zones의 null/빈 리스트와 null/빈 원소는 모두 여기서 막는다. 그렇지 않으면
 * 도메인 계층의 {@code validateZonesUsedByPerformance}가 {@code usableZones.containsAll(null)}
 * 에서 NullPointerException을 던져 STB 에러코드 대신 500으로 새어나간다.
 * 개수(1~3)·중복 등 리스트 전체에 대한 규칙은 {@code Standby.validateZones}에서 검증한다.
 */
public record CreateStandbyRequest(
        @NotNull(message = "사용자 정보 입력은 필수입니다.") Long userId,
        @NotNull(message = "공연 정보 입력은 필수입니다.") Long performanceId,
        @NotNull(message = "회차 정보 입력은 필수입니다.") Long sessionNum,
        @NotEmpty(message = "지망 구역은 최소 1개 이상 입력해야 합니다.")
        List<@NotBlank(message = "지망 구역 값은 비어있을 수 없습니다.") String> zones
) {
}
