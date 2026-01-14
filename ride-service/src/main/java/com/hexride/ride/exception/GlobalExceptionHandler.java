package com.hexride.ride.exception;

import com.hexride.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RideNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleRideNotFound(RideNotFoundException ex) {
        log.warn("Ride not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("RIDE_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(FareNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleFareNotFound(FareNotFoundException ex) {
        log.warn("Fare not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("FARE_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(InvalidRideStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidRideState(InvalidRideStateException ex) {
        log.warn("Invalid ride state: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("INVALID_RIDE_STATE", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_ERROR", "An unexpected error occurred"));
    }
}
