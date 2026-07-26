package com.programmers.kdt.standby.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record StandbyTicketRequest(
        Long ticketId,
        Long standbyUserId,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime standbyExpiredAt
) {
}
