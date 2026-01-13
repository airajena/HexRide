package com.hexride.location.service;

import com.hexride.location.dto.DriverLocationResponse;
import com.hexride.location.dto.LocationUpdateRequest;

import java.util.List;

public interface LocationService {

    void updateLocation(LocationUpdateRequest request);

    DriverLocationResponse getDriverLocation(String driverId);

    List<DriverLocationResponse> getNearbyDrivers(Double lat, Double lng, Integer radiusKm, Integer limit);

    List<String> getNearbyDriverIds(Double lat, Double lng, Integer radiusKm);

    void removeDriverLocation(String driverId);
}
