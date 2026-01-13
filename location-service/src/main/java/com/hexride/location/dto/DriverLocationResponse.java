package com.hexride.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverLocationResponse {

    private String driverId;
    private Double latitude;
    private Double longitude;
    private String h3Index;
    private Double heading;
    private Double speed;
    private Instant lastUpdated;
}
