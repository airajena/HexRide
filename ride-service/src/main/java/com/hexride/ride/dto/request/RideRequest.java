package com.hexride.ride.dto.request;

import com.hexride.common.dto.LocationDto;
import com.hexride.common.enums.RideType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideRequest {

    @NotBlank
    private String fareId;

    @NotBlank
    private String riderId;

    @NotNull
    private RideType rideType;

    @NotNull
    private LocationDto pickupLocation;

    @NotNull
    private LocationDto dropoffLocation;
}
