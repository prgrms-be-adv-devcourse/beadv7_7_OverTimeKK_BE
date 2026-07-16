package com.programmers.kdt.performance.dto;

public record CreateStandbyResponse(
        Long standbyId,
        String zone,
        String status
) {
}
