package com.hexride.ride.controller;

import com.hexride.common.dto.ApiResponse;
import com.hexride.ride.dto.request.FareEstimateRequest;
import com.hexride.ride.dto.response.FareEstimateResponse;
import com.hexride.ride.service.FareService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fares")
@RequiredArgsConstructor
public class FareController {

    private final FareService fareService;

    @PostMapping("/estimate")
    public ResponseEntity<ApiResponse<FareEstimateResponse>> estimateFare(
            @Valid @RequestBody FareEstimateRequest request) {
        FareEstimateResponse response = fareService.estimateFare(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{fareId}")
    public ResponseEntity<ApiResponse<FareEstimateResponse>> getFare(@PathVariable String fareId) {
        FareEstimateResponse response = fareService.getFareById(fareId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
