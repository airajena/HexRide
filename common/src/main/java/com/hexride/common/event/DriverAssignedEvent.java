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
public class DriverAssignedEvent {
    
    private String rideId;
    private String riderId;
    private String driverId;
    private String vehicleId;
    private String driverName;
    private String vehiclePlate;
    private String vehicleModel;
    private String vehicleColor;
    private Double driverRating;
    private Integer etaSeconds;
    private Instant timestamp;
}
