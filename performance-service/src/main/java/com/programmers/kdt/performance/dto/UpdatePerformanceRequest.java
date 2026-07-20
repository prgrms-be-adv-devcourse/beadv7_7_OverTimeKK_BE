package com.programmers.kdt.performance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UpdatePerformanceRequest(
        @NotBlank String title, String description, @NotBlank String runtime,
        @NotNull LocalDate startDate, @NotNull LocalDate endDate,
        LocalDateTime ticketOpenAt, @NotNull Long hallId
) {
}
