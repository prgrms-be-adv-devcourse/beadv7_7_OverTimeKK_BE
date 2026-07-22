package com.programmers.kdt.performance.service;

import com.programmers.kdt.performance.dto.RegisterPerformanceRequest;
import com.programmers.kdt.performance.entity.Performance;
import com.programmers.kdt.performance.repository.PerformanceRepository;
import com.programmers.kdt.performance.repository.PerformanceSeatPriceRepository;
import com.programmers.kdt.performance.repository.PerformanceSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerformanceV2Service {

    private final PerformanceRepository performanceRepository;
    private final PerformanceSessionRepository sessionRepository;
    private final PerformanceSeatPriceRepository seatPriceRepository;

    public void registerPerformanceInformation(RegisterPerformanceRequest request) {
        // 1. 공연등록
        Performance performance = performanceRepository.save(request.toPerformance());

        // 2. 회차등록
        sessionRepository.saveAll(request.toSessions(performance));

        // 3. 좌석 등급별 금액 등록
        seatPriceRepository.saveAll(request.toSeatPrices(performance));
    }
}
