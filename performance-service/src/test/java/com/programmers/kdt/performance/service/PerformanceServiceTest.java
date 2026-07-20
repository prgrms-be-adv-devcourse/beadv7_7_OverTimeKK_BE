package com.programmers.kdt.performance.service;

import com.programmers.kdt.performance.dto.RegisterPerformanceRequest;
import com.programmers.kdt.performance.dto.RegisterPerformanceResponse;
import com.programmers.kdt.performance.entity.Performance;
import com.programmers.kdt.performance.repository.PerformanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PerformanceServiceTest {

    @Mock
    private PerformanceRepository performanceRepository;

    @InjectMocks
    private PerformanceService performanceService;

    @Test
    void 공연_등록_성공() {
        // given
        RegisterPerformanceRequest request = new RegisterPerformanceRequest(
                "뮤지컬A", "설명", "120분",
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31),
                null, 1L);
        Long sellerId = 1L;

        Performance saved = Performance.create(
                "뮤지컬A", "설명", "120분",
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31),
                null, sellerId, 1L);
        ReflectionTestUtils.setField(saved, "performanceId", 1L);   // @Id는 setter가 없어 테스트에서 주입
        given(performanceRepository.save(any(Performance.class))).willReturn(saved);

        // when
        RegisterPerformanceResponse res = performanceService.registerPerformance(request, sellerId);

        // then
        assertThat(res.performanceId()).isEqualTo(1L);
        assertThat(res.title()).isEqualTo("뮤지컬A");
        verify(performanceRepository).save(any(Performance.class));
    }
}
