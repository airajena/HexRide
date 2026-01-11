package com.hexride.driver.dto.request;

import com.hexride.common.enums.RideType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRegistrationRequest {

    @NotBlank
    private String userId;

    @NotBlank
    private String licenseNumber;

    @NotBlank
    private String licensePlate;

    @NotBlank
    private String make;

    @NotBlank
    private String model;

    @NotBlank
    private String color;

    private Integer year;

    @NotEmpty
    private Set<RideType> supportedRideTypes;
}
