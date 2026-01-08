package com.hexride.common.exception;

public class RideNotFoundException extends RuntimeException {
    
    public RideNotFoundException(String rideId) {
        super("Ride not found: " + rideId);
    }
}
