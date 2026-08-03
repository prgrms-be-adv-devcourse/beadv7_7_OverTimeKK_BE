package com.programmers.kdt.user.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * 사용자당 유효한 refresh token(jti)을 1개만 Redis에 보관한다.
 * 기기당 1세션만 허용 - 새로 저장하면 이전 세션은 자동으로 무효화된다.
 */
@Component
public class RefreshTokenStore {

    private static final String KEY_PREFIX = "refresh:user:";

    private final StringRedisTemplate redisTemplate;
    private final Duration ttl;

    public RefreshTokenStore(StringRedisTemplate redisTemplate,
                              @Value("${jwt.refresh-expiration-millis}") long refreshExpirationMillis) {
        this.redisTemplate = redisTemplate;
        this.ttl = Duration.ofMillis(refreshExpirationMillis);
    }

    public void save(Long userId, String tokenId) {
        redisTemplate.opsForValue().set(key(userId), tokenId, ttl);
    }

    public Optional<String> find(Long userId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key(userId)));
    }

    public void delete(Long userId) {
        redisTemplate.delete(key(userId));
    }

    private String key(Long userId) {
        return KEY_PREFIX + userId;
    }
}
