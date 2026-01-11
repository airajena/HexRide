package com.hexride.driver.service.impl;

import com.hexride.common.constants.RedisKeys;
import com.hexride.common.enums.DriverStatus;
import com.hexride.common.exception.DriverNotAvailableException;
import com.hexride.common.util.H3Utils;
import com.hexride.driver.dto.request.AvailabilityRequest;
import com.hexride.driver.dto.request.DriverRegistrationRequest;
import com.hexride.driver.dto.response.DriverResponse;
import com.hexride.driver.entity.Driver;
import com.hexride.driver.entity.Vehicle;
import com.hexride.driver.mapper.DriverMapper;
import com.hexride.driver.repository.DriverRepository;
import com.hexride.driver.repository.VehicleRepository;
import com.hexride.driver.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final StringRedisTemplate redisTemplate;
    private final DriverMapper driverMapper;

    @Override
    @Transactional
    public DriverResponse registerDriver(DriverRegistrationRequest request) {
        if (driverRepository.existsById(request.getUserId())) {
            throw new IllegalArgumentException("Driver already registered");
        }

        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new IllegalArgumentException("License number already exists");
        }

        if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new IllegalArgumentException("Vehicle license plate already exists");
        }

        Driver driver = Driver.builder()
                .id(request.getUserId())
                .licenseNumber(request.getLicenseNumber())
                .status(DriverStatus.OFFLINE)
                .build();

        driver = driverRepository.save(driver);

        Vehicle vehicle = Vehicle.builder()
                .driver(driver)
                .licensePlate(request.getLicensePlate())
                .make(request.getMake())
                .model(request.getModel())
                .color(request.getColor())
                .year(request.getYear())
                .supportedRideTypes(request.getSupportedRideTypes())
                .build();

        vehicleRepository.save(vehicle);
        driver.setVehicle(vehicle);

        return driverMapper.toResponse(driver);
    }

    @Override
    public DriverResponse getDriver(String driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotAvailableException(driverId));
        return driverMapper.toResponse(driver);
    }

    @Override
    @Transactional
    public DriverResponse updateAvailability(String driverId, AvailabilityRequest request) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotAvailableException(driverId));

        if (request.getIsAvailable()) {
            driver.setStatus(DriverStatus.ONLINE);
            
            if (request.getCurrentLocation() != null) {
                String h3Index = H3Utils.coordsToH3(
                        request.getCurrentLocation().getLatitude(),
                        request.getCurrentLocation().getLongitude()
                );
                
                Map<String, String> locationData = new HashMap<>();
                locationData.put("lat", String.valueOf(request.getCurrentLocation().getLatitude()));
                locationData.put("lng", String.valueOf(request.getCurrentLocation().getLongitude()));
                locationData.put("h3Index", h3Index);
                locationData.put("timestamp", String.valueOf(System.currentTimeMillis()));
                
                redisTemplate.opsForHash().putAll(RedisKeys.driverLocation(driverId), locationData);
                redisTemplate.expire(RedisKeys.driverLocation(driverId), Duration.ofMinutes(5));
                
                redisTemplate.opsForSet().add(RedisKeys.driversByH3(h3Index), driverId);
                redisTemplate.opsForSet().add(RedisKeys.DRIVERS_ONLINE, driverId);
            }
        } else {
            driver.setStatus(DriverStatus.OFFLINE);
            
            String locationKey = RedisKeys.driverLocation(driverId);
            Object h3Index = redisTemplate.opsForHash().get(locationKey, "h3Index");
            
            if (h3Index != null) {
                redisTemplate.opsForSet().remove(RedisKeys.driversByH3(h3Index.toString()), driverId);
            }
            
            redisTemplate.delete(locationKey);
            redisTemplate.opsForSet().remove(RedisKeys.DRIVERS_ONLINE, driverId);
        }

        driver = driverRepository.save(driver);
        return driverMapper.toResponse(driver);
    }

    @Override
    public List<DriverResponse> getDriversByIds(List<String> driverIds) {
        return driverRepository.findAllById(driverIds).stream()
                .map(driverMapper::toResponse)
                .collect(Collectors.toList());
    }
}
