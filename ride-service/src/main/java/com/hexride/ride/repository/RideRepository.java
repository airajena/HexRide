package com.hexride.ride.repository;

import com.hexride.common.enums.RideStatus;
import com.hexride.ride.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, String> {

    List<Ride> findByRiderId(String riderId);

    List<Ride> findByDriverId(String driverId);

    List<Ride> findByRiderIdAndStatus(String riderId, RideStatus status);

    List<Ride> findByDriverIdAndStatus(String driverId, RideStatus status);

    @Query("SELECT r FROM Ride r WHERE r.driverId = :driverId AND r.status IN ('ACCEPTED', 'DRIVER_ARRIVED', 'IN_TRANSIT')")
    Optional<Ride> findActiveRideByDriverId(String driverId);

    @Query("SELECT r FROM Ride r WHERE r.riderId = :riderId AND r.status IN ('REQUESTED', 'ACCEPTED', 'DRIVER_ARRIVED', 'IN_TRANSIT')")
    Optional<Ride> findActiveRideByRiderId(String riderId);
}
