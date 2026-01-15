package com.hexride.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationUpdateMessage {

    private String rideId;
    private String driverId;
    private Double latitude;
    private Double longitude;
    private Double heading;
    private Double speed;
    private Instant timestamp;
}
