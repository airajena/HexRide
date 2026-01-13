package com.hexride.location.controller;

import com.hexride.common.dto.ApiResponse;
import com.hexride.location.dto.DriverLocationResponse;
import com.hexride.location.dto.LocationUpdateRequest;
import com.hexride.location.dto.NearbyDriversRequest;
import com.hexride.location.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<Void>> updateLocation(
            @Valid @RequestBody LocationUpdateRequest request) {
        locationService.updateLocation(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/drivers/{driverId}")
    public ResponseEntity<ApiResponse<DriverLocationResponse>> getDriverLocation(
            @PathVariable String driverId) {
        DriverLocationResponse response = locationService.getDriverLocation(driverId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/nearby")
    public ResponseEntity<ApiResponse<List<DriverLocationResponse>>> getNearbyDrivers(
            @Valid @RequestBody NearbyDriversRequest request) {
        List<DriverLocationResponse> drivers = locationService.getNearbyDrivers(
                request.getLatitude(),
                request.getLongitude(),
                request.getRadiusKm(),
                request.getLimit()
        );
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }

    @GetMapping("/nearby/ids")
    public ResponseEntity<ApiResponse<List<String>>> getNearbyDriverIds(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "5") Integer radiusKm) {
        List<String> driverIds = locationService.getNearbyDriverIds(lat, lng, radiusKm);
        return ResponseEntity.ok(ApiResponse.success(driverIds));
    }

    @DeleteMapping("/drivers/{driverId}")
    public ResponseEntity<ApiResponse<Void>> removeDriverLocation(@PathVariable String driverId) {
        locationService.removeDriverLocation(driverId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
