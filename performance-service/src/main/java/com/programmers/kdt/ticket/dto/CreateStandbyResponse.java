package com.programmers.kdt.ticket.dto;

import java.util.List;

public record CreateStandbyResponse(
        Long standbyId,
        List<String> zones,
        String status
) {
}
