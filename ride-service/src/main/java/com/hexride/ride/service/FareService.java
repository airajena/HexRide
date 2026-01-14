package com.hexride.ride.service;

import com.hexride.ride.dto.request.FareEstimateRequest;
import com.hexride.ride.dto.response.FareEstimateResponse;

public interface FareService {

    FareEstimateResponse estimateFare(FareEstimateRequest request);

    FareEstimateResponse getFareById(String fareId);
}
