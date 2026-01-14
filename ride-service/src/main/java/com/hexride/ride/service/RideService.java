package com.hexride.ride.service;

import com.hexride.ride.dto.request.RideRequest;
import com.hexride.ride.dto.response.RideResponse;

import java.util.List;

public interface RideService {

    RideResponse createRide(RideRequest request);

    RideResponse getRide(String rideId);

    RideResponse acceptRide(String rideId, String driverId);

    RideResponse driverArrived(String rideId, String driverId);

    RideResponse startRide(String rideId, String driverId, String otp);

    RideResponse completeRide(String rideId, String driverId);

    RideResponse cancelRide(String rideId, String cancelledBy, String reason);

    List<RideResponse> getRiderHistory(String riderId);

    List<RideResponse> getDriverHistory(String driverId);
}
