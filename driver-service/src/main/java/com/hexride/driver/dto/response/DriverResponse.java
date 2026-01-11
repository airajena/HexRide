package com.hexride.driver.dto.response;

import com.hexride.common.enums.DriverStatus;
import com.hexride.common.enums.RideType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {

    private String id;
    private String licenseNumber;
    private Double rating;
    private Integer totalRides;
    private DriverStatus status;
    private Double acceptanceRate;
    private VehicleResponse vehicle;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleResponse {
        private String id;
        private String licensePlate;
        private String make;
        private String model;
        private String color;
        private Integer year;
        private Set<RideType> supportedRideTypes;
    }
}
