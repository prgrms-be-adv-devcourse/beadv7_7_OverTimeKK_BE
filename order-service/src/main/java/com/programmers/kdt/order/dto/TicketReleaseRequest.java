package com.programmers.kdt.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketReleaseRequest(

        @NotBlank
        String holdKey

) {
}