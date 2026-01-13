package com.hexride.location.dto;

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
public class LocationUpdateRequest {

    @NotBlank
    private String driverId;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private Double heading;

    private Double speed;
}
