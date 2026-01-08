package com.hexride.common.event;

import com.hexride.common.dto.LocationDto;
import com.hexride.common.enums.RideType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestedEvent {
    
    private String rideId;
    private String riderId;
    private LocationDto pickupLocation;
    private LocationDto dropoffLocation;
    private RideType rideType;
    private Double estimatedFare;
    private Instant timestamp;
}
