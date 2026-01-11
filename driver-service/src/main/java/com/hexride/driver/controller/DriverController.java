package com.hexride.driver.controller;

import com.hexride.common.dto.ApiResponse;
import com.hexride.driver.dto.request.AvailabilityRequest;
import com.hexride.driver.dto.request.DriverRegistrationRequest;
import com.hexride.driver.dto.response.DriverResponse;
import com.hexride.driver.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<DriverResponse>> register(@Valid @RequestBody DriverRegistrationRequest request) {
        DriverResponse response = driverService.registerDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/{driverId}")
    public ResponseEntity<ApiResponse<DriverResponse>> getDriver(@PathVariable String driverId) {
        DriverResponse response = driverService.getDriver(driverId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{driverId}/availability")
    public ResponseEntity<ApiResponse<DriverResponse>> updateAvailability(
            @PathVariable String driverId,
            @Valid @RequestBody AvailabilityRequest request) {
        DriverResponse response = driverService.updateAvailability(driverId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<DriverResponse>>> getDriversByIds(@RequestBody List<String> driverIds) {
        List<DriverResponse> responses = driverService.getDriversByIds(driverIds);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
