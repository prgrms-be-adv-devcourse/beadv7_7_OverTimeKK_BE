package com.programmers.kdt.payment.service;

import java.util.Optional;

public interface IdempotencyKeyService {
    Optional<String> generate(String idempotencyKey);

    void complete(String idempotencyKey, String responseBody);

    void release(String idempotencyKey);

}
