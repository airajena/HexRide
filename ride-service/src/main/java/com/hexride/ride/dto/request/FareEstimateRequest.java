package com.hexride.ride.dto.request;

import com.hexride.common.dto.LocationDto;
import com.hexride.common.enums.RideType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareEstimateRequest {

    @NotNull
    private LocationDto pickupLocation;

    @NotNull
    private LocationDto dropoffLocation;

    @NotNull
    private RideType rideType;
}
