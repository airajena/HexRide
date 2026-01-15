package com.hexride.websocket.dto;

import com.hexride.common.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideStatusMessage {

    private String rideId;
    private String riderId;
    private String driverId;
    private RideStatus previousStatus;
    private RideStatus newStatus;
    private Instant timestamp;
}
