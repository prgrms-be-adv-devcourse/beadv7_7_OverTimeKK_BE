package com.programmers.kdt.payment.service;

import com.programmers.kdt.payment.entity.key.IdempotencyKey;

import java.util.Optional;

public interface IdempotencyKeyService {
    Optional<String> generate(String idempotencyKey);

    void complete(String idempotencyKey, String responseBody);


}
