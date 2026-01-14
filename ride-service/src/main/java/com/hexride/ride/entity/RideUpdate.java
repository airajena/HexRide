package com.hexride.ride.entity;

import com.hexride.common.enums.RideStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ride_updates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String rideId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus newStatus;

    private String updatedBy;

    private String notes;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
