package com.programmers.kdt.performance.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.programmers.kdt.performance.entity.Performance;
import com.programmers.kdt.performance.entity.PerformanceSession;
import com.programmers.kdt.performance.entity.PerformanceSessionId;

import java.time.LocalDateTime;

public record PerformanceSessionRequest(
        Long sessionId,
        Long performanceId,
        String actor,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime performanceStartAt
) {

    public PerformanceSession toEntity(Performance performance) {
        return PerformanceSession.create(
                new PerformanceSessionId(sessionId, performanceId),
                performance,
                actor,
                performanceStartAt);
    }
}
