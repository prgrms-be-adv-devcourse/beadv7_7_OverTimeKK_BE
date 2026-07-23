package com.programmers.kdt.ticket.controller;

import com.programmers.kdt.common.response.ApiResponse;
import com.programmers.kdt.ticket.dto.CreateStandbyRequest;
import com.programmers.kdt.ticket.dto.CreateStandbyResponse;
import com.programmers.kdt.ticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/standby")
    public ApiResponse<CreateStandbyResponse> issueStandby(@Valid @RequestBody CreateStandbyRequest request) {
        CreateStandbyResponse response = ticketService.issueStandby(
                request.userId(), request.performanceId(), request.sessionNum(), request.zones());
        return ApiResponse.success(response);
    }
}
