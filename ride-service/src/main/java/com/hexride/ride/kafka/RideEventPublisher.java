package com.hexride.ride.kafka;

import com.hexride.common.enums.RideStatus;
import com.hexride.common.event.RideRequestedEvent;
import com.hexride.common.event.RideStatusChangedEvent;
import com.hexride.ride.entity.Ride;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RideEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String RIDE_REQUESTED_TOPIC = "ride.requested";
    private static final String RIDE_STATUS_CHANGED_TOPIC = "ride.status.changed";

    public void publishRideRequested(RideRequestedEvent event) {
        log.info("Publishing ride requested event: {}", event.getRideId());
        kafkaTemplate.send(RIDE_REQUESTED_TOPIC, event.getRideId(), event);
    }

    public void publishStatusChange(Ride ride, RideStatus previousStatus) {
        RideStatusChangedEvent event = RideStatusChangedEvent.builder()
                .rideId(ride.getId())
                .riderId(ride.getRiderId())
                .driverId(ride.getDriverId())
                .previousStatus(previousStatus)
                .newStatus(ride.getStatus())
                .build();

        log.info("Publishing ride status changed: {} -> {}", previousStatus, ride.getStatus());
        kafkaTemplate.send(RIDE_STATUS_CHANGED_TOPIC, ride.getId(), event);
    }
}
