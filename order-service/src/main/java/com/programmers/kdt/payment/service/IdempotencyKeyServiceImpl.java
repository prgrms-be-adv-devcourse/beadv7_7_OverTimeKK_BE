package com.programmers.kdt.payment.service;

import com.programmers.kdt.common.exception.BusinessException;
import com.programmers.kdt.payment.entity.key.IdempotencyKey;
import com.programmers.kdt.payment.exception.PaymentErrorCode;
import com.programmers.kdt.payment.repository.IdempotencyKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyKeyServiceImpl implements IdempotencyKeyService {

    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final IdempotencyKeyTxOps txOps;

    @Override
    public Optional<String> generate(String idempotencyKey) {
        try {
            txOps.tryInsert(idempotencyKey);
            return Optional.empty();
        } catch (DataIntegrityViolationException e) { // 이미 있는 멱등키를 다시 저장하려고 하는 경우
            IdempotencyKey existing = txOps.findFresh(idempotencyKey)
                    .orElseThrow(() -> e);

            if (existing.isExpired()) {
                txOps.expireAndReinsert(idempotencyKey, existing);
                return Optional.empty();
            }

            if (existing.getResponseBody() == null) {
                throw new BusinessException(PaymentErrorCode.PAYMENT_ALREADY_EXISTS);
            }
            return Optional.of(existing.getResponseBody());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void complete(String idempotencyKey, String responseBody) {
        int updated = idempotencyKeyRepository.findById(idempotencyKey)
                .map(key -> { key.complete(responseBody); return 1; })
                .orElse(0);
        log.info("[IDEMPOTENCY_COMPLETE] key={}, found={}, bodyLen={}", idempotencyKey, updated, responseBody == null ? -1 : responseBody.length());
    }

}
