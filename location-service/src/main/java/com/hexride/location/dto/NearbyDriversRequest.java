package com.hexride.location.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyDriversRequest {

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private Integer radiusKm = 5;

    private Integer limit = 10;
}
