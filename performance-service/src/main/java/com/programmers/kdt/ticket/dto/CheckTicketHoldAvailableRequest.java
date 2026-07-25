package com.programmers.kdt.ticket.dto;

import jakarta.validation.constraints.NotNull;

public record CheckTicketHoldAvailableRequest(
        @NotNull Long ticketId,
        @NotNull Long userId
) {
}
