package com.hexride.ride.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexride.common.util.H3Utils;
import com.hexride.ride.dto.request.FareEstimateRequest;
import com.hexride.ride.dto.response.FareEstimateResponse;
import com.hexride.ride.exception.FareNotFoundException;
import com.hexride.ride.service.FareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FareServiceImpl implements FareService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${fare.base-rate:50.0}")
    private Double baseRate;

    @Value("${fare.per-km-rate:12.0}")
    private Double perKmRate;

    @Value("${fare.per-minute-rate:2.0}")
    private Double perMinuteRate;

    @Value("${fare.cache-ttl-seconds:120}")
    private Integer cacheTtlSeconds;

    @Override
    public FareEstimateResponse estimateFare(FareEstimateRequest request) {
        log.info("Estimating fare for ride type: {}", request.getRideType());

        // Calculate distance using Haversine formula
        double distanceKm = H3Utils.getDistanceKm(
                request.getPickupLocation().getLatitude(),
                request.getPickupLocation().getLongitude(),
                request.getDropoffLocation().getLatitude(),
                request.getDropoffLocation().getLongitude()
        );

        // Estimate time (average 3 minutes per km in city)
        int estimatedDurationSeconds = (int) (distanceKm * 3 * 60);

        // Calculate fare components
        double baseFare = baseRate;
        double distanceFare = distanceKm * perKmRate;
        double timeFare = (estimatedDurationSeconds / 60.0) * perMinuteRate;

        // Get surge multiplier from Redis (if exists)
        String pickupH3 = H3Utils.coordsToH3(
                request.getPickupLocation().getLatitude(),
                request.getPickupLocation().getLongitude()
        );
        double surgeMultiplier = getSurgeMultiplier(pickupH3);

        // Calculate total with surge
        double totalFare = (baseFare + distanceFare + timeFare) * surgeMultiplier;

        // Build response
        FareEstimateResponse response = FareEstimateResponse.builder()
                .fareId("fare_" + UUID.randomUUID().toString().substring(0, 8))
                .rideType(request.getRideType())
                .baseFare(Math.round(baseFare * 100.0) / 100.0)
                .distanceFare(Math.round(distanceFare * 100.0) / 100.0)
                .timeFare(Math.round(timeFare * 100.0) / 100.0)
                .surgeMultiplier(surgeMultiplier)
                .totalFare(Math.round(totalFare * 100.0) / 100.0)
                .distanceKm(Math.round(distanceKm * 100.0) / 100.0)
                .estimatedDurationSeconds(estimatedDurationSeconds)
                .currency("INR")
                .build();

        // Cache fare estimate in Redis
        cacheFareEstimate(response);

        log.info("Fare estimated: {} for fareId: {}", response.getTotalFare(), response.getFareId());
        return response;
    }

    @Override
    public FareEstimateResponse getFareById(String fareId) {
        String fareJson = redisTemplate.opsForValue().get(fareId);
        if (fareJson == null) {
            throw new FareNotFoundException(fareId);
        }

        try {
            return objectMapper.readValue(fareJson, FareEstimateResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Error parsing cached fare: {}", fareId, e);
            throw new FareNotFoundException(fareId);
        }
    }

    private void cacheFareEstimate(FareEstimateResponse response) {
        try {
            String fareJson = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(
                    response.getFareId(),
                    fareJson,
                    Duration.ofSeconds(cacheTtlSeconds)
            );
        } catch (JsonProcessingException e) {
            log.error("Error caching fare estimate: {}", response.getFareId(), e);
        }
    }

    private double getSurgeMultiplier(String h3Index) {
        String surgeKey = "surge:" + h3Index;
        String surgeValue = redisTemplate.opsForValue().get(surgeKey);
        if (surgeValue != null) {
            try {
                return Double.parseDouble(surgeValue);
            } catch (NumberFormatException e) {
                log.warn("Invalid surge value for h3: {}", h3Index);
            }
        }
        return 1.0; // Default no surge
    }
}
