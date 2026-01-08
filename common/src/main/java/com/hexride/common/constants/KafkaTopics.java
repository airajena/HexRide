package com.hexride.common.constants;

public final class KafkaTopics {
    
    private KafkaTopics() {}
    
    public static final String RIDE_REQUESTED = "ride.requested";
    public static final String RIDE_STATUS_CHANGED = "ride.status.changed";
    public static final String DRIVER_LOCATION_UPDATED = "driver.location.updated";
    public static final String DRIVER_ASSIGNED = "driver.assigned";
}
