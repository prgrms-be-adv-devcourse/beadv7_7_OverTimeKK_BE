package com.programmers.kdt.standby.controller;

import com.programmers.kdt.common.exception.BusinessException;
import com.programmers.kdt.common.exception.CommonErrorCode;
import com.programmers.kdt.common.jwt.JwtAuthFilter;
import com.programmers.kdt.common.response.ApiResponse;
import com.programmers.kdt.standby.dto.CreateStandbyRequest;
import com.programmers.kdt.standby.dto.CreateStandbyResponse;
import com.programmers.kdt.standby.dto.StandbyRankResponse;
import com.programmers.kdt.standby.service.StandbyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/standby")
@RequiredArgsConstructor
public class StandbyController {

    private final StandbyService standbyService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateStandbyResponse>> issueStandby(@Valid @RequestBody CreateStandbyRequest request) {
        CreateStandbyResponse response = standbyService.applyStandby(
                request.userId(), request.performanceId(), request.sessionNum(), request.zones());
        return ResponseEntity.created(URI.create("/api/standby/" + response.standbyId()))
                .body(ApiResponse.success(response));
    }

    @GetMapping("/{standbyId}")
    public  ApiResponse<StandbyRankResponse> getStandbyRank(
            @PathVariable Long standbyId,
            @RequestAttribute(value = JwtAuthFilter.USER_ID_ATTRIBUTE, required = false) Long userId) {
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }
        StandbyRankResponse response = standbyService.getStandbyRank(standbyId,userId);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{standbyId}")
    public ResponseEntity<Void> cancelStandby(
            @PathVariable Long standbyId,
            @RequestAttribute(value = JwtAuthFilter.USER_ID_ATTRIBUTE, required = false) Long userId) {
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }
        standbyService.cancelStandby(standbyId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{standbyId}/zones/{zone}")
    public ResponseEntity<Void> cancelStandbyZone(
            @PathVariable Long standbyId,
            @PathVariable String zone,
            @RequestAttribute(value = JwtAuthFilter.USER_ID_ATTRIBUTE, required = false) Long userId) {
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }
        standbyService.cancelZone(standbyId, userId, zone);
        return ResponseEntity.noContent().build();
    }
}
