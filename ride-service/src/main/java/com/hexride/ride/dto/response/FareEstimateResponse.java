package com.hexride.ride.dto.response;

import com.hexride.common.enums.RideType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareEstimateResponse {

    private String fareId;
    private RideType rideType;
    private Double baseFare;
    private Double distanceFare;
    private Double timeFare;
    private Double surgeMultiplier;
    private Double totalFare;
    private Double distanceKm;
    private Integer estimatedDurationSeconds;
    private String currency;
}
