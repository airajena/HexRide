package com.hexride.ride.service.impl;

import com.hexride.common.enums.RideStatus;
import com.hexride.common.event.RideRequestedEvent;
import com.hexride.common.util.H3Utils;
import com.hexride.ride.dto.request.RideRequest;
import com.hexride.ride.dto.response.FareEstimateResponse;
import com.hexride.ride.dto.response.RideResponse;
import com.hexride.ride.entity.Ride;
import com.hexride.ride.entity.RideUpdate;
import com.hexride.ride.exception.InvalidRideStateException;
import com.hexride.ride.exception.RideNotFoundException;
import com.hexride.ride.kafka.RideEventPublisher;
import com.hexride.ride.mapper.RideMapper;
import com.hexride.ride.repository.RideRepository;
import com.hexride.ride.repository.RideUpdateRepository;
import com.hexride.ride.service.FareService;
import com.hexride.ride.service.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideUpdateRepository rideUpdateRepository;
    private final RideMapper rideMapper;
    private final FareService fareService;
    private final RideEventPublisher eventPublisher;

    @Override
    @Transactional
    public RideResponse createRide(RideRequest request) {
        log.info("Creating ride for rider: {}", request.getRiderId());

        // Get fare estimate from cache
        FareEstimateResponse fare = fareService.getFareById(request.getFareId());

        // Calculate H3 index for pickup location
        String pickupH3 = H3Utils.coordsToH3(
                request.getPickupLocation().getLatitude(),
                request.getPickupLocation().getLongitude()
        );

        // Generate 4-digit OTP for ride verification
        String otp = String.format("%04d", new Random().nextInt(10000));

        // Create ride entity
        Ride ride = Ride.builder()
                .riderId(request.getRiderId())
                .rideType(request.getRideType())
                .pickupLatitude(request.getPickupLocation().getLatitude())
                .pickupLongitude(request.getPickupLocation().getLongitude())
                .pickupAddress(request.getPickupLocation().getAddress())
                .pickupH3Index(pickupH3)
                .dropoffLatitude(request.getDropoffLocation().getLatitude())
                .dropoffLongitude(request.getDropoffLocation().getLongitude())
                .dropoffAddress(request.getDropoffLocation().getAddress())
                .estimatedFare(fare.getTotalFare())
                .surgeMultiplier(fare.getSurgeMultiplier())
                .distanceKm(fare.getDistanceKm())
                .estimatedDurationSeconds(fare.getEstimatedDurationSeconds())
                .otp(otp)
                .status(RideStatus.REQUESTED)
                .build();

        ride = rideRepository.save(ride);
        log.info("Ride created with id: {}", ride.getId());

        // Publish ride requested event for matching service
        eventPublisher.publishRideRequested(RideRequestedEvent.builder()
                .rideId(ride.getId())
                .riderId(ride.getRiderId())
                .rideType(ride.getRideType())
                .pickupLocation(com.hexride.common.dto.LocationDto.builder()
                        .latitude(ride.getPickupLatitude())
                        .longitude(ride.getPickupLongitude())
                        .h3Index(pickupH3)
                        .build())
                .dropoffLocation(com.hexride.common.dto.LocationDto.builder()
                        .latitude(ride.getDropoffLatitude())
                        .longitude(ride.getDropoffLongitude())
                        .build())
                .estimatedFare(ride.getEstimatedFare())
                .build());

        return rideMapper.toResponse(ride);
    }

    @Override
    public RideResponse getRide(String rideId) {
        Ride ride = findRideById(rideId);
        return rideMapper.toResponse(ride);
    }

    @Override
    @Transactional
    public RideResponse acceptRide(String rideId, String driverId) {
        Ride ride = findRideById(rideId);

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new InvalidRideStateException("Ride cannot be accepted in current state: " + ride.getStatus());
        }

        RideStatus previousStatus = ride.getStatus();
        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ACCEPTED);
        ride.setAcceptedAt(LocalDateTime.now());

        ride = rideRepository.save(ride);
        saveRideUpdate(ride.getId(), previousStatus, RideStatus.ACCEPTED, driverId);
        eventPublisher.publishStatusChange(ride, previousStatus);

        log.info("Ride {} accepted by driver {}", rideId, driverId);
        return rideMapper.toResponse(ride);
    }

    @Override
    @Transactional
    public RideResponse driverArrived(String rideId, String driverId) {
        Ride ride = validateDriverAction(rideId, driverId, RideStatus.ACCEPTED);

        RideStatus previousStatus = ride.getStatus();
        ride.setStatus(RideStatus.DRIVER_ARRIVED);
        ride.setArrivedAt(LocalDateTime.now());

        ride = rideRepository.save(ride);
        saveRideUpdate(ride.getId(), previousStatus, RideStatus.DRIVER_ARRIVED, driverId);
        eventPublisher.publishStatusChange(ride, previousStatus);

        log.info("Driver {} arrived for ride {}", driverId, rideId);
        return rideMapper.toResponse(ride);
    }

    @Override
    @Transactional
    public RideResponse startRide(String rideId, String driverId, String otp) {
        Ride ride = validateDriverAction(rideId, driverId, RideStatus.DRIVER_ARRIVED);

        // Verify OTP
        if (!ride.getOtp().equals(otp)) {
            throw new InvalidRideStateException("Invalid OTP");
        }

        RideStatus previousStatus = ride.getStatus();
        ride.setStatus(RideStatus.IN_TRANSIT);
        ride.setStartedAt(LocalDateTime.now());

        ride = rideRepository.save(ride);
        saveRideUpdate(ride.getId(), previousStatus, RideStatus.IN_TRANSIT, driverId);
        eventPublisher.publishStatusChange(ride, previousStatus);

        log.info("Ride {} started", rideId);
        return rideMapper.toResponse(ride);
    }

    @Override
    @Transactional
    public RideResponse completeRide(String rideId, String driverId) {
        Ride ride = validateDriverAction(rideId, driverId, RideStatus.IN_TRANSIT);

        RideStatus previousStatus = ride.getStatus();
        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());
        ride.setFinalFare(ride.getEstimatedFare()); // In real app, recalculate based on actual route

        ride = rideRepository.save(ride);
        saveRideUpdate(ride.getId(), previousStatus, RideStatus.COMPLETED, driverId);
        eventPublisher.publishStatusChange(ride, previousStatus);

        log.info("Ride {} completed. Final fare: {}", rideId, ride.getFinalFare());
        return rideMapper.toResponse(ride);
    }

    @Override
    @Transactional
    public RideResponse cancelRide(String rideId, String cancelledBy, String reason) {
        Ride ride = findRideById(rideId);

        if (ride.getStatus() == RideStatus.COMPLETED || ride.getStatus() == RideStatus.CANCELLED) {
            throw new InvalidRideStateException("Ride cannot be cancelled in current state: " + ride.getStatus());
        }

        RideStatus previousStatus = ride.getStatus();
        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancelledAt(LocalDateTime.now());
        ride.setCancellationReason(reason);

        ride = rideRepository.save(ride);
        saveRideUpdate(ride.getId(), previousStatus, RideStatus.CANCELLED, cancelledBy);
        eventPublisher.publishStatusChange(ride, previousStatus);

        log.info("Ride {} cancelled by {}. Reason: {}", rideId, cancelledBy, reason);
        return rideMapper.toResponse(ride);
    }

    @Override
    public List<RideResponse> getRiderHistory(String riderId) {
        return rideRepository.findByRiderId(riderId).stream()
                .map(rideMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RideResponse> getDriverHistory(String driverId) {
        return rideRepository.findByDriverId(driverId).stream()
                .map(rideMapper::toResponse)
                .collect(Collectors.toList());
    }

    private Ride findRideById(String rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException(rideId));
    }

    private Ride validateDriverAction(String rideId, String driverId, RideStatus expectedStatus) {
        Ride ride = findRideById(rideId);

        if (!driverId.equals(ride.getDriverId())) {
            throw new InvalidRideStateException("Driver not assigned to this ride");
        }

        if (ride.getStatus() != expectedStatus) {
            throw new InvalidRideStateException("Invalid ride state: " + ride.getStatus() + ", expected: " + expectedStatus);
        }

        return ride;
    }

    private void saveRideUpdate(String rideId, RideStatus previous, RideStatus newStatus, String updatedBy) {
        RideUpdate update = RideUpdate.builder()
                .rideId(rideId)
                .previousStatus(previous)
                .newStatus(newStatus)
                .updatedBy(updatedBy)
                .build();
        rideUpdateRepository.save(update);
    }
}
