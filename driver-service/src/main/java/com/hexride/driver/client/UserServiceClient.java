package com.hexride.driver.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.user-service-url:http://localhost:8081}")
    private String userServiceUrl;

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
    @Retry(name = "userService")
    public Map<String, Object> getUser(String userId) {
        String url = userServiceUrl + "/api/v1/users/" + userId;
        return restTemplate.getForObject(url, Map.class);
    }

    public Map<String, Object> getUserFallback(String userId, Exception ex) {
        log.warn("Fallback triggered for getUser: {}", ex.getMessage());
        return Map.of(
                "id", userId,
                "firstName", "Unknown",
                "lastName", "User",
                "fallback", true
        );
    }
}
