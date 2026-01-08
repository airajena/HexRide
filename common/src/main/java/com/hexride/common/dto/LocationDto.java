package com.hexride.common.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    
    @NotNull
    private Double latitude;
    
    @NotNull
    private Double longitude;
    
    private String address;
    
    private String h3Index;
}
