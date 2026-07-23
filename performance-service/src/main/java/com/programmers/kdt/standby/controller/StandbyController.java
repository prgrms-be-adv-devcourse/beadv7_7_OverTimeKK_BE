package com.programmers.kdt.standby.controller;

import com.programmers.kdt.common.response.ApiResponse;
import com.programmers.kdt.standby.dto.CreateStandbyRequest;
import com.programmers.kdt.standby.dto.CreateStandbyResponse;
import com.programmers.kdt.standby.service.StandbyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @DeleteMapping("/{standbyId}")
    public ResponseEntity<Void> cancelStandby(
            @PathVariable Long standbyId,
            @RequestHeader("X-User-Id") Long userId) {
        standbyService.cancelStandby(standbyId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{standbyId}/zones/{zone}")
    public ResponseEntity<Void> cancelStandbyZone(
            @PathVariable Long standbyId,
            @PathVariable String zone,
            @RequestHeader("X-User-Id") Long userId) {
        standbyService.cancelZone(standbyId, userId, zone);
        return ResponseEntity.noContent().build();
    }
}
