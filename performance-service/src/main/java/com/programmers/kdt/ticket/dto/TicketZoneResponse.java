package com.programmers.kdt.ticket.dto;

public record TicketZoneResponse(
        Long ticketId,
        String seatRow,
        String seatNum
) {
}
