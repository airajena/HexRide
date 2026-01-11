package com.hexride.driver.mapper;

import com.hexride.driver.dto.response.DriverResponse;
import com.hexride.driver.entity.Driver;
import com.hexride.driver.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DriverMapper {
    
    @Mapping(target = "vehicle", source = "vehicle")
    DriverResponse toResponse(Driver driver);
    
    DriverResponse.VehicleResponse toVehicleResponse(Vehicle vehicle);
}
