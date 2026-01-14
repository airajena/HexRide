package com.hexride.ride.controller;

import com.hexride.common.dto.ApiResponse;
import com.hexride.ride.dto.request.RideRequest;
import com.hexride.ride.dto.response.RideResponse;
import com.hexride.ride.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @PostMapping
    public ResponseEntity<ApiResponse<RideResponse>> createRide(@Valid @RequestBody RideRequest request) {
        RideResponse response = rideService.createRide(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/{rideId}")
    public ResponseEntity<ApiResponse<RideResponse>> getRide(@PathVariable String rideId) {
        RideResponse response = rideService.getRide(rideId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{rideId}/accept")
    public ResponseEntity<ApiResponse<RideResponse>> acceptRide(
            @PathVariable String rideId,
            @RequestParam String driverId) {
        RideResponse response = rideService.acceptRide(rideId, driverId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{rideId}/driver-arrived")
    public ResponseEntity<ApiResponse<RideResponse>> driverArrived(
            @PathVariable String rideId,
            @RequestParam String driverId) {
        RideResponse response = rideService.driverArrived(rideId, driverId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{rideId}/start")
    public ResponseEntity<ApiResponse<RideResponse>> startRide(
            @PathVariable String rideId,
            @RequestParam String driverId,
            @RequestParam String otp) {
        RideResponse response = rideService.startRide(rideId, driverId, otp);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{rideId}/complete")
    public ResponseEntity<ApiResponse<RideResponse>> completeRide(
            @PathVariable String rideId,
            @RequestParam String driverId) {
        RideResponse response = rideService.completeRide(rideId, driverId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{rideId}/cancel")
    public ResponseEntity<ApiResponse<RideResponse>> cancelRide(
            @PathVariable String rideId,
            @RequestBody Map<String, String> body) {
        String cancelledBy = body.get("cancelledBy");
        String reason = body.get("reason");
        RideResponse response = rideService.cancelRide(rideId, cancelledBy, reason);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/rider/{riderId}/history")
    public ResponseEntity<ApiResponse<List<RideResponse>>> getRiderHistory(@PathVariable String riderId) {
        List<RideResponse> history = rideService.getRiderHistory(riderId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/driver/{driverId}/history")
    public ResponseEntity<ApiResponse<List<RideResponse>>> getDriverHistory(@PathVariable String driverId) {
        List<RideResponse> history = rideService.getDriverHistory(driverId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
