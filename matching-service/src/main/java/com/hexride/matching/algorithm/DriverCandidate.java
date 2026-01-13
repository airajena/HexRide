package com.hexride.matching.algorithm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverCandidate {
    private String driverId;
    private Double latitude;
    private Double longitude;
    private Double rating;
    private Double acceptanceRate;
    private Double distanceKm;
    private Double score;
}
