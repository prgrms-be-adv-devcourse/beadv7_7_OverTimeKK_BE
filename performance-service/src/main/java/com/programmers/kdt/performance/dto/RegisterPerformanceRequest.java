package com.programmers.kdt.performance.dto;

import com.programmers.kdt.performance.entity.Performance;

import java.util.List;

public record RegisterPerformanceRequest(
        Long userId,
        PerformanceV2Request performanceRequest,
        List<RegisterPerformanceSessionRequest> sessionRequests,
        List<PerformanceSeatPriceRequest> seatPriceRequests
) {
    public Performance toPerformance() {
        return performanceRequest.toPerformance(userId);
    }
}
