package com.hexride.driver.dto.request;

import com.hexride.common.dto.LocationDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityRequest {

    @NotNull
    private Boolean isAvailable;

    private LocationDto currentLocation;
}
