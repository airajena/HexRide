package com.hexride.ride.dto.response;

import com.hexride.common.enums.RideStatus;
import com.hexride.common.enums.RideType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideResponse {

    private String id;
    private String riderId;
    private String driverId;
    private RideStatus status;
    private RideType rideType;

    private Double pickupLatitude;
    private Double pickupLongitude;
    private String pickupAddress;

    private Double dropoffLatitude;
    private Double dropoffLongitude;
    private String dropoffAddress;

    private Double estimatedFare;
    private Double finalFare;
    private Double surgeMultiplier;
    private Double distanceKm;
    private Integer estimatedDurationSeconds;

    private String otp;

    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime arrivedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
