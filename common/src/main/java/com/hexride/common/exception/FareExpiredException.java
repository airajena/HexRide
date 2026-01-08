package com.hexride.common.exception;

public class FareExpiredException extends RuntimeException {
    
    public FareExpiredException(String fareId) {
        super("Fare quote expired: " + fareId);
    }
}
