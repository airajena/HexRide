package com.hexride.matching.algorithm;

import com.hexride.common.util.H3Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DriverScorer {

    private static final double DISTANCE_WEIGHT = 0.5;
    private static final double RATING_WEIGHT = 0.3;
    private static final double ACCEPTANCE_RATE_WEIGHT = 0.2;
    private static final double MAX_DISTANCE_KM = 10.0;

    public List<DriverCandidate> scoreAndRank(List<DriverCandidate> candidates,
                                               Double pickupLat, Double pickupLng) {
        return candidates.stream()
                .map(candidate -> {
                    double distance = H3Utils.getDistanceKm(
                            pickupLat, pickupLng,
                            candidate.getLatitude(), candidate.getLongitude()
                    );
                    candidate.setDistanceKm(distance);
                    candidate.setScore(calculateScore(candidate));
                    return candidate;
                })
                .sorted(Comparator.comparingDouble(DriverCandidate::getScore).reversed())
                .collect(Collectors.toList());
    }

    private double calculateScore(DriverCandidate candidate) {
        // Distance score: closer driver = higher score (0 to 1)
        double distanceScore = Math.max(0, 1 - (candidate.getDistanceKm() / MAX_DISTANCE_KM));

        // Rating score: 5.0 = 1.0, 4.0 = 0.8, etc.
        double ratingScore = (candidate.getRating() != null ? candidate.getRating() : 5.0) / 5.0;

        // Acceptance rate: direct percentage (0 to 1)
        double acceptanceScore = candidate.getAcceptanceRate() != null ? candidate.getAcceptanceRate() : 1.0;

        // Weighted sum
        double score = (distanceScore * DISTANCE_WEIGHT) +
                (ratingScore * RATING_WEIGHT) +
                (acceptanceScore * ACCEPTANCE_RATE_WEIGHT);

        log.debug("Driver {} score: {} (distance: {}, rating: {}, acceptance: {})",
                candidate.getDriverId(), score, distanceScore, ratingScore, acceptanceScore);

        return score;
    }
}
