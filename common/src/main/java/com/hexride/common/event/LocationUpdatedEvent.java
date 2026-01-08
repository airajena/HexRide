package com.hexride.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationUpdatedEvent {
    
    private String driverId;
    private String rideId;
    private Double latitude;
    private Double longitude;
    private String h3Index;
    private Double heading;
    private Double speed;
    private Instant timestamp;
}
