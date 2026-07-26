package com.programmers.kdt.standby.dto;

import jakarta.validation.constraints.NotNull;

public record TryMatchRequest(
        @NotNull(message = "티켓 정보를 입력해주세요.")
        Long ticketId
) {
}
