package com.programmers.kdt.settlement.dto;

public record SettlementSessionResponse(
        Long sellerId,
        Long performanceId,
        Long sessionNum
) {
}
