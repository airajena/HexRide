package com.hexride.matching.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockService {

    private final StringRedisTemplate redisTemplate;

    @Value("${matching.lock-expiry-seconds:30}")
    private Integer lockExpirySeconds;

    private static final String LOCK_PREFIX = "lock:ride:";

    public boolean acquireLock(String rideId, String driverId) {
        String lockKey = LOCK_PREFIX + rideId;

        // SETNX with expiry - atomic operation
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, driverId, Duration.ofSeconds(lockExpirySeconds));

        if (Boolean.TRUE.equals(acquired)) {
            log.debug("Lock acquired for ride: {} by driver: {}", rideId, driverId);
            return true;
        }

        log.debug("Failed to acquire lock for ride: {}", rideId);
        return false;
    }

    public void releaseLock(String rideId) {
        String lockKey = LOCK_PREFIX + rideId;
        redisTemplate.delete(lockKey);
        log.debug("Lock released for ride: {}", rideId);
    }
}
