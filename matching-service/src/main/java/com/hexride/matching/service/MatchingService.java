package com.hexride.matching.service;

import com.hexride.common.event.RideRequestedEvent;

public interface MatchingService {

    void findAndAssignDriver(RideRequestedEvent event);
}
