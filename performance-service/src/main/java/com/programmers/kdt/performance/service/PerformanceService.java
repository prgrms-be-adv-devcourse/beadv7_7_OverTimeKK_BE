package com.programmers.kdt.performance.service;

import com.programmers.kdt.performance.dto.RegisterPerformanceRequest;
import com.programmers.kdt.performance.dto.RegisterPerformanceResponse;
import com.programmers.kdt.performance.entity.Performance;
import com.programmers.kdt.performance.repository.PerformanceRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    @Transactional
    public RegisterPerformanceResponse registerPerformance(RegisterPerformanceRequest request, Long sellerId) {
        // TODO: hallId 존재 검증(venue), sellerId 판매자 검증(user)
        Performance performance = Performance.create(
                request.title(),
                request.description(),
                request.runtime(),
                request.startDate(),
                request.endDate(),
                request.ticketOpenAt(),
                sellerId,
                request.hallId());

        Performance saved = performanceRepository.save(performance);
        return new RegisterPerformanceResponse(saved.getPerformanceId(), saved.getTitle());
    }

    public void cancelPerformance(Long performanceId) {
        // TODO: order-service에 REST로 취소/환불 요청 (OrderClient 필요)
    }
}
