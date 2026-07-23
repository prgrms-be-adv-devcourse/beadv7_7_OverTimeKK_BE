package com.programmers.kdt.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateStandbyRequest(
        @NotNull(message = "사용자 정보 입력은 필수입니다.") Long userId,
        @NotNull(message = "공연 정보 입력은 필수입니다.") Long performanceId,
        @NotNull(message = "회차 정보 입력은 필수입니다.") Long sessionNum,
        @NotEmpty(message = "지망 구역은 최소 1개 이상 입력해야 합니다.")
        List<@NotBlank(message = "지망 구역 값은 비어있을 수 없습니다.") String> zones
) {
}
