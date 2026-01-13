package com.hexride.matching.kafka;

import com.hexride.common.event.RideRequestedEvent;
import com.hexride.matching.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RideRequestedConsumer {

    private final MatchingService matchingService;

    @KafkaListener(topics = "ride.requested", groupId = "matching-service")
    public void handleRideRequested(RideRequestedEvent event) {
        log.info("Received ride request: {}", event.getRideId());
        try {
            matchingService.findAndAssignDriver(event);
        } catch (Exception e) {
            log.error("Error processing ride request: {}", event.getRideId(), e);
        }
    }
}
