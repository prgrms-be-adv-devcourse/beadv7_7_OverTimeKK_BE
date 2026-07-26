package com.programmers.kdt.order.dto;

public record TicketHoldRequest(
        Long ticketId,
        Long userId
) {
    public static TicketHoldRequest from(CreateOrderRequest request){
        return new TicketHoldRequest(request.ticketId(), request.userId());
    }
}
