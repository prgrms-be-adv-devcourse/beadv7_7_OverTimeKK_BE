package com.programmers.kdt.payment.client;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class MockPgClient implements PgClient {

    @Override
    public PgReadyResult ready(PgReadyCommand command) {
        String transactionKey = "Mock-" + UUID.randomUUID();
        return new PgReadyResult(transactionKey, "https://mock-pg.local/pay/" + transactionKey);
    }

    @Override
    public PgApproveResult approve(PgApproveCommand command) {
        return new PgApproveResult(true, LocalDateTime.now());
    }

    @Override
    public PgCancelResult cancel(PgCancelCommand command) {
        return new PgCancelResult(true, LocalDateTime.now());
    }
}
