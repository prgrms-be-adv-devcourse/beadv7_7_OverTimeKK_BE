package com.programmers.kdt.standby.client;

import com.programmers.kdt.standby.dto.StandbyTicketRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

@Component
public class TicketClient {

    private final RestClient restClient;

    public TicketClient(@Value("${server.port}") String port) {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    public void notifyMatched(Long ticketId, Long standbyUserId, LocalDateTime standbyExpiredAt) {
        restClient.put()
                .uri("/api/tickets/standby")
                .body(new StandbyTicketRequest (ticketId, standbyUserId, standbyExpiredAt))
                .retrieve()
                .toBodilessEntity();
    }

}
