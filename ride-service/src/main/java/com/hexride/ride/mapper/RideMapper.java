package com.hexride.ride.mapper;

import com.hexride.ride.dto.response.RideResponse;
import com.hexride.ride.entity.Ride;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RideMapper {

    RideResponse toResponse(Ride ride);
}
