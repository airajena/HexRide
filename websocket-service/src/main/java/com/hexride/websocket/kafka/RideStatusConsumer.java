package com.hexride.websocket.kafka;

import com.hexride.common.event.RideStatusChangedEvent;
import com.hexride.websocket.dto.RideStatusMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RideStatusConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "ride.status.changed", groupId = "websocket-service")
    public void handleStatusChange(RideStatusChangedEvent event) {
        RideStatusMessage message = RideStatusMessage.builder()
                .rideId(event.getRideId())
                .riderId(event.getRiderId())
                .driverId(event.getDriverId())
                .previousStatus(event.getPreviousStatus())
                .newStatus(event.getNewStatus())
                .timestamp(event.getTimestamp())
                .build();

        messagingTemplate.convertAndSend(
                "/topic/ride/" + event.getRideId() + "/status",
                message
        );

        if (event.getRiderId() != null) {
            messagingTemplate.convertAndSendToUser(
                    event.getRiderId(),
                    "/queue/ride-updates",
                    message
            );
        }

        if (event.getDriverId() != null) {
            messagingTemplate.convertAndSendToUser(
                    event.getDriverId(),
                    "/queue/ride-updates",
                    message
            );
        }

        log.debug("Ride status change broadcasted: {} -> {}", 
                event.getPreviousStatus(), event.getNewStatus());
    }
}
