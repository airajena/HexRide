package com.hexride.location.repository;

import com.hexride.location.dto.DriverLocationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DriverLocationRepository {

    private final StringRedisTemplate redisTemplate;

    @Value("${location.ttl-seconds:300}")
    private Integer ttlSeconds;

    private static final String DRIVER_LOCATION_PREFIX = "driver:loc:";
    private static final String H3_PREFIX = "h3:";
    private static final String DRIVERS_ONLINE = "drivers:online";

    public void saveLocation(String driverId, Double latitude, Double longitude,
                            String h3Index, Double heading, Double speed) {
        String key = DRIVER_LOCATION_PREFIX + driverId;

        // Remove from old H3 cell if moved
        Object oldH3 = redisTemplate.opsForHash().get(key, "h3Index");
        if (oldH3 != null && !oldH3.toString().equals(h3Index)) {
            redisTemplate.opsForSet().remove(H3_PREFIX + oldH3, driverId);
        }

        // Store location data as hash
        Map<String, String> data = new HashMap<>();
        data.put("lat", String.valueOf(latitude));
        data.put("lng", String.valueOf(longitude));
        data.put("h3Index", h3Index);
        data.put("heading", heading != null ? String.valueOf(heading) : "0");
        data.put("speed", speed != null ? String.valueOf(speed) : "0");
        data.put("timestamp", String.valueOf(System.currentTimeMillis()));

        redisTemplate.opsForHash().putAll(key, data);
        redisTemplate.expire(key, Duration.ofSeconds(ttlSeconds));

        // Add to H3 cell set for proximity queries
        redisTemplate.opsForSet().add(H3_PREFIX + h3Index, driverId);

        // Mark driver as online
        redisTemplate.opsForSet().add(DRIVERS_ONLINE, driverId);

        log.debug("Saved location for driver: {} at h3: {}", driverId, h3Index);
    }

    public Optional<DriverLocationResponse> getLocation(String driverId) {
        String key = DRIVER_LOCATION_PREFIX + driverId;
        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);

        if (data.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(DriverLocationResponse.builder()
                .driverId(driverId)
                .latitude(Double.parseDouble(data.get("lat").toString()))
                .longitude(Double.parseDouble(data.get("lng").toString()))
                .h3Index(data.get("h3Index").toString())
                .heading(Double.parseDouble(data.get("heading").toString()))
                .speed(Double.parseDouble(data.get("speed").toString()))
                .lastUpdated(Instant.ofEpochMilli(Long.parseLong(data.get("timestamp").toString())))
                .build());
    }

    public Set<String> getDriversInH3Cells(Set<String> h3Indices) {
        Set<String> driverIds = new HashSet<>();
        for (String h3Index : h3Indices) {
            Set<String> drivers = redisTemplate.opsForSet().members(H3_PREFIX + h3Index);
            if (drivers != null) {
                driverIds.addAll(drivers);
            }
        }
        return driverIds;
    }

    public void removeLocation(String driverId) {
        String key = DRIVER_LOCATION_PREFIX + driverId;

        Object h3Index = redisTemplate.opsForHash().get(key, "h3Index");
        if (h3Index != null) {
            redisTemplate.opsForSet().remove(H3_PREFIX + h3Index, driverId);
        }

        redisTemplate.delete(key);
        redisTemplate.opsForSet().remove(DRIVERS_ONLINE, driverId);

        log.info("Removed location for driver: {}", driverId);
    }
}
