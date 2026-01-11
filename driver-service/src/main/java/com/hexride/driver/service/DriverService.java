package com.hexride.driver.service;

import com.hexride.driver.dto.request.AvailabilityRequest;
import com.hexride.driver.dto.request.DriverRegistrationRequest;
import com.hexride.driver.dto.response.DriverResponse;

import java.util.List;

public interface DriverService {
    
    DriverResponse registerDriver(DriverRegistrationRequest request);
    
    DriverResponse getDriver(String driverId);
    
    DriverResponse updateAvailability(String driverId, AvailabilityRequest request);
    
    List<DriverResponse> getDriversByIds(List<String> driverIds);
}
