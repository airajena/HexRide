package com.hexride.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user")
    public Mono<ResponseEntity<Map<String, Object>>> userServiceFallback() {
        return createFallbackResponse("user-service");
    }

    @GetMapping("/driver")
    public Mono<ResponseEntity<Map<String, Object>>> driverServiceFallback() {
        return createFallbackResponse("driver-service");
    }

    @GetMapping("/ride")
    public Mono<ResponseEntity<Map<String, Object>>> rideServiceFallback() {
        return createFallbackResponse("ride-service");
    }

    @GetMapping("/location")
    public Mono<ResponseEntity<Map<String, Object>>> locationServiceFallback() {
        return createFallbackResponse("location-service");
    }

    @GetMapping
    public Mono<ResponseEntity<Map<String, Object>>> defaultFallback() {
        return createFallbackResponse("unknown-service");
    }

    private Mono<ResponseEntity<Map<String, Object>>> createFallbackResponse(String serviceName) {
        Map<String, Object> response = Map.of(
                "success", false,
                "error", Map.of(
                        "code", "SERVICE_UNAVAILABLE",
                        "message", serviceName + " is currently unavailable. Please try again later."
                ),
                "timestamp", Instant.now().toString()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
}
