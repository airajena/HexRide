package com.hexride.ride.exception;

public class FareNotFoundException extends RuntimeException {
    public FareNotFoundException(String fareId) {
        super("Fare estimate not found or expired: " + fareId);
    }
}
