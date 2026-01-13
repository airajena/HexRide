package com.hexride.matching.service.impl;

import com.hexride.common.event.DriverAssignedEvent;
import com.hexride.common.event.RideRequestedEvent;
import com.hexride.common.util.H3Utils;
import com.hexride.matching.algorithm.DriverCandidate;
import com.hexride.matching.algorithm.DriverScorer;
import com.hexride.matching.lock.DistributedLockService;
import com.hexride.matching.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingServiceImpl implements MatchingService {

    private final StringRedisTemplate redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final DriverScorer driverScorer;
    private final DistributedLockService lockService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${matching.max-attempts:5}")
    private Integer maxAttempts;

    @Value("${matching.search-radius-km:5}")
    private Integer searchRadiusKm;

    @Value("${services.ride-service-url:http://localhost:8082}")
    private String rideServiceUrl;

    @Override
    public void findAndAssignDriver(RideRequestedEvent event) {
        log.info("Finding driver for ride: {}", event.getRideId());

        // Find nearby drivers using H3
        List<DriverCandidate> candidates = findNearbyDrivers(
                event.getPickupLocation().getLatitude(),
                event.getPickupLocation().getLongitude()
        );

        if (candidates.isEmpty()) {
            log.warn("No drivers found for ride: {}", event.getRideId());
            return;
        }

        log.info("Found {} candidate drivers for ride: {}", candidates.size(), event.getRideId());

        // Score and rank candidates
        List<DriverCandidate> rankedCandidates = driverScorer.scoreAndRank(
                candidates,
                event.getPickupLocation().getLatitude(),
                event.getPickupLocation().getLongitude()
        );

        // Try to assign top candidates (with distributed lock)
        for (int i = 0; i < Math.min(maxAttempts, rankedCandidates.size()); i++) {
            DriverCandidate candidate = rankedCandidates.get(i);

            if (tryAssignDriver(event.getRideId(), candidate.getDriverId())) {
                // Call ride-service to accept the ride
                acceptRideForDriver(event.getRideId(), candidate.getDriverId());

                // Publish driver assigned event
                publishDriverAssigned(event, candidate);
                return;
            }
        }

        log.warn("Failed to assign driver after {} attempts for ride: {}", maxAttempts, event.getRideId());
    }

    private List<DriverCandidate> findNearbyDrivers(Double lat, Double lng) {
        List<DriverCandidate> candidates = new ArrayList<>();

        // Get center H3 cell
        String centerH3 = H3Utils.coordsToH3(lat, lng);

        // Get all H3 cells in search area
        int kRings = Math.max(1, searchRadiusKm / 2);
        Set<String> searchArea = H3Utils.getKRing(centerH3, kRings);

        // Get drivers in those cells
        for (String h3Index : searchArea) {
            Set<String> drivers = redisTemplate.opsForSet().members("h3:" + h3Index);
            if (drivers != null) {
                for (String driverId : drivers) {
                    DriverCandidate candidate = getDriverCandidate(driverId);
                    if (candidate != null) {
                        candidates.add(candidate);
                    }
                }
            }
        }

        return candidates;
    }

    private DriverCandidate getDriverCandidate(String driverId) {
        String key = "driver:loc:" + driverId;
        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);

        if (data.isEmpty()) {
            return null;
        }

        return DriverCandidate.builder()
                .driverId(driverId)
                .latitude(Double.parseDouble(data.get("lat").toString()))
                .longitude(Double.parseDouble(data.get("lng").toString()))
                .rating(5.0) // Default, should fetch from driver-service
                .acceptanceRate(1.0) // Default
                .build();
    }

    private boolean tryAssignDriver(String rideId, String driverId) {
        // Acquire distributed lock for ride
        if (!lockService.acquireLock(rideId, driverId)) {
            return false;
        }

        try {
            // Check if driver already has an active ride
            String activeRide = redisTemplate.opsForValue().get("active:ride:" + driverId);
            if (activeRide != null) {
                log.debug("Driver {} already has active ride: {}", driverId, activeRide);
                return false;
            }

            // Mark driver as busy
            redisTemplate.opsForValue().set(
                    "active:ride:" + driverId,
                    rideId,
                    Duration.ofHours(2)
            );

            log.info("Successfully assigned driver {} to ride {}", driverId, rideId);
            return true;
        } finally {
            lockService.releaseLock(rideId);
        }
    }

    private void acceptRideForDriver(String rideId, String driverId) {
        try {
            String url = rideServiceUrl + "/api/v1/rides/" + rideId + "/accept?driverId=" + driverId;
            restTemplate.put(url, null);
            log.info("Ride {} accepted by driver {}", rideId, driverId);
        } catch (Exception e) {
            log.error("Failed to accept ride {} for driver {}", rideId, driverId, e);
        }
    }

    private void publishDriverAssigned(RideRequestedEvent event, DriverCandidate driver) {
        DriverAssignedEvent assignedEvent = DriverAssignedEvent.builder()
                .rideId(event.getRideId())
                .riderId(event.getRiderId())
                .driverId(driver.getDriverId())
                .etaSeconds((int) (driver.getDistanceKm() * 3 * 60)) // 3 min per km
                .build();

        kafkaTemplate.send("driver.assigned", event.getRideId(), assignedEvent);
        log.info("Published driver.assigned event for ride: {}", event.getRideId());
    }
}
