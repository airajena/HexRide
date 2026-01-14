package com.hexride.ride.entity;

import com.hexride.common.enums.RideStatus;
import com.hexride.common.enums.RideType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String riderId;

    private String driverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RideStatus status = RideStatus.REQUESTED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideType rideType;

    // Pickup location
    @Column(nullable = false)
    private Double pickupLatitude;

    @Column(nullable = false)
    private Double pickupLongitude;

    private String pickupAddress;

    @Column(name = "pickuph3index")
    private String pickupH3Index;

    // Dropoff location
    @Column(nullable = false)
    private Double dropoffLatitude;

    @Column(nullable = false)
    private Double dropoffLongitude;

    private String dropoffAddress;

    // Fare details
    private Double estimatedFare;
    private Double finalFare;

    @Builder.Default
    private Double surgeMultiplier = 1.0;

    private Double distanceKm;
    private Integer estimatedDurationSeconds;

    // Security - 4 digit OTP for ride verification
    private String otp;

    // Timestamps
    @CreationTimestamp
    private LocalDateTime requestedAt;

    private LocalDateTime acceptedAt;
    private LocalDateTime arrivedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
