package com.hexride.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverLocationDto {
    
    private String driverId;
    private Double latitude;
    private Double longitude;
    private String h3Index;
    private Double heading;
    private Double speed;
    private Long timestamp;
}
