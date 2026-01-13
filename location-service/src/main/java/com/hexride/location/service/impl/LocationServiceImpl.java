package com.hexride.location.service.impl;

import com.hexride.common.util.H3Utils;
import com.hexride.location.dto.DriverLocationResponse;
import com.hexride.location.dto.LocationUpdateRequest;
import com.hexride.location.repository.DriverLocationRepository;
import com.hexride.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {

    private final DriverLocationRepository locationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void updateLocation(LocationUpdateRequest request) {
        log.debug("Updating location for driver: {}", request.getDriverId());

        // Convert coordinates to H3 index
        String h3Index = H3Utils.coordsToH3(request.getLatitude(), request.getLongitude());

        // Save to Redis
        locationRepository.saveLocation(
                request.getDriverId(),
                request.getLatitude(),
                request.getLongitude(),
                h3Index,
                request.getHeading(),
                request.getSpeed()
        );

        // Publish location update event
        Map<String, Object> event = new HashMap<>();
        event.put("driverId", request.getDriverId());
        event.put("latitude", request.getLatitude());
        event.put("longitude", request.getLongitude());
        event.put("h3Index", h3Index);
        event.put("timestamp", System.currentTimeMillis());

        kafkaTemplate.send("driver.location.updated", request.getDriverId(), event);
    }

    @Override
    public DriverLocationResponse getDriverLocation(String driverId) {
        return locationRepository.getLocation(driverId)
                .orElseThrow(() -> new RuntimeException("Driver location not found: " + driverId));
    }

    @Override
    public List<DriverLocationResponse> getNearbyDrivers(Double lat, Double lng, Integer radiusKm, Integer limit) {
        List<String> driverIds = getNearbyDriverIds(lat, lng, radiusKm);

        List<DriverLocationResponse> drivers = new ArrayList<>();
        for (String driverId : driverIds) {
            locationRepository.getLocation(driverId).ifPresent(drivers::add);
        }

        // Sort by distance
        drivers.sort(Comparator.comparingDouble(d ->
                H3Utils.getDistanceKm(lat, lng, d.getLatitude(), d.getLongitude())));

        return drivers.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<String> getNearbyDriverIds(Double lat, Double lng, Integer radiusKm) {
        // Get center H3 cell
        String centerH3 = H3Utils.coordsToH3(lat, lng);

        // Calculate how many rings to search (roughly 2km per ring)
        int kRings = Math.max(1, radiusKm / 2);

        // Get all H3 cells in search area
        Set<String> searchArea = H3Utils.getKRing(centerH3, kRings);

        // Get all drivers in those cells
        return new ArrayList<>(locationRepository.getDriversInH3Cells(searchArea));
    }

    @Override
    public void removeDriverLocation(String driverId) {
        locationRepository.removeLocation(driverId);
    }
}
