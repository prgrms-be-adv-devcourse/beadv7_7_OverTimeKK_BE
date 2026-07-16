package com.programmers.kdt.performance.controller;

import com.programmers.kdt.performance.dto.CreateStandbyResponse;
import com.programmers.kdt.performance.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/standby")
    public CreateStandbyResponse issueStandby(
            @RequestParam Long userId, @RequestParam Long sessionNum, @RequestParam String zone) {
        return ticketService.issueStandby(userId, sessionNum, zone);
    }
}
