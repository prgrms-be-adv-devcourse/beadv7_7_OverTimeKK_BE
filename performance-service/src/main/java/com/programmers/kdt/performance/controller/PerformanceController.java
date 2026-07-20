package com.programmers.kdt.performance.controller;

import com.programmers.kdt.common.response.ApiResponse;
import com.programmers.kdt.performance.dto.RegisterPerformanceRequest;
import com.programmers.kdt.performance.dto.RegisterPerformanceResponse;
import com.programmers.kdt.performance.service.PerformanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

import java.net.URI;

@RestController
@RequestMapping("/api/performances")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;

    // TODO: user-service 판매자 검증필요
    @PostMapping
    public ResponseEntity<ApiResponse<RegisterPerformanceResponse>> register(@Valid @RequestBody RegisterPerformanceRequest request, @RequestHeader("X-User-Id") Long sellerId) {
        RegisterPerformanceResponse res = performanceService.registerPerformance(request, sellerId);
        return ResponseEntity.created(URI.create("/api/performances/" + res.performanceId())).body(ApiResponse.success(res));
    }

    @PostMapping("/{performanceId}/cancel")
    public void cancel(@PathVariable Long performanceId) {
        performanceService.cancelPerformance(performanceId);
    }
}
