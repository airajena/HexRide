package com.hexride.websocket.kafka;

import com.hexride.common.event.LocationUpdatedEvent;
import com.hexride.websocket.dto.LocationUpdateMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocationUpdateConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "driver.location.updated", groupId = "websocket-service")
    public void handleLocationUpdate(LocationUpdatedEvent event) {
        if (event.getRideId() != null) {
            LocationUpdateMessage message = LocationUpdateMessage.builder()
                    .rideId(event.getRideId())
                    .driverId(event.getDriverId())
                    .latitude(event.getLatitude())
                    .longitude(event.getLongitude())
                    .heading(event.getHeading())
                    .speed(event.getSpeed())
                    .timestamp(event.getTimestamp())
                    .build();

            messagingTemplate.convertAndSend(
                    "/topic/ride/" + event.getRideId() + "/location",
                    message
            );
            
            log.debug("Location update broadcasted for ride: {}", event.getRideId());
        }
    }
}
