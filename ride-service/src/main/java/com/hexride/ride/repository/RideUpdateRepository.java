package com.hexride.ride.repository;

import com.hexride.ride.entity.RideUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideUpdateRepository extends JpaRepository<RideUpdate, String> {

    List<RideUpdate> findByRideIdOrderByCreatedAtDesc(String rideId);
}
