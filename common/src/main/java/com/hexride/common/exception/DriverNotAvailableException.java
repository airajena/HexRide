package com.hexride.common.exception;

public class DriverNotAvailableException extends RuntimeException {
    
    public DriverNotAvailableException(String driverId) {
        super("Driver not available: " + driverId);
    }
}
