package com.programmers.kdt.payment.client.pg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

// 실제 PG사가 아닌 모킹으로 우선 구현(토스페이로 교체 예정)
@Profile("!toss")
@Component
public class MockPgClient implements PgClient {

    private final Map<String, Queue<Supplier<PgApproveResult>>> approveBehaviors = new ConcurrentHashMap<>();
    private final Map<String, Queue<Supplier<PgApproveResult>>> selectBehaviors = new ConcurrentHashMap<>();

    @Value("${pg.mock.approve-delay-ms:0}")
    private long approveDelayMs;

    public void stubApprove(String paymentKey, Supplier<PgApproveResult> behavior) {
        approveBehaviors.computeIfAbsent(paymentKey, k -> new ConcurrentLinkedQueue<>()).add(behavior);
    }

    public void stubSelect(String paymentKey, Supplier<PgApproveResult> behavior) {
        selectBehaviors.computeIfAbsent(paymentKey, k -> new ConcurrentLinkedQueue<>()).add(behavior);
    }

    public void reset() {
        approveBehaviors.clear();
        selectBehaviors.clear();
    }

    @Override
    public PgReadyResult ready(PgReadyCommand command) {
        String transactionKey = "Mock-" + UUID.randomUUID();
        String orderId = PgOrderIdFormatter.format(command.orderId());
        return new PgReadyResult(transactionKey, orderId, "https://mock-pg.local/pay/" + transactionKey);
    }

    @Override
    public PgApproveResult approve(PgApproveCommand command) {
        sleepIfConfigured();
        return resolve(approveBehaviors, command.transactionKey());
    }

    @Override
    public PgCancelResult cancel(PgCancelCommand command) {
        return new PgCancelResult(true, LocalDateTime.now());
    }

    @Override
    public PgApproveResult select(String paymentKey) {
        return resolve(selectBehaviors, paymentKey);
    }

    private void sleepIfConfigured() {
        if (approveDelayMs <= 0) return;
        try {
            Thread.sleep(approveDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private PgApproveResult resolve(Map<String, Queue<Supplier<PgApproveResult>>> behaviors, String key) {
        Queue<Supplier<PgApproveResult>> queue = behaviors.get(key);
        if (queue == null || queue.isEmpty()) {
            return new PgApproveResult(true, LocalDateTime.now());
        }
        Supplier<PgApproveResult> behavior = queue.size() > 1 ? queue.poll() : queue.peek();
        return behavior.get();
    }
}
