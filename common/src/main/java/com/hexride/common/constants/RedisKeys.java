package com.hexride.common.constants;

public final class RedisKeys {
    
    private RedisKeys() {}
    
    public static final String DRIVER_LOCATION_PREFIX = "driver:location:";
    public static final String DRIVER_H3_PREFIX = "h3:drivers:";
    public static final String DRIVERS_ONLINE = "drivers:online";
    public static final String RIDE_LOCK_PREFIX = "lock:ride:";
    public static final String FARE_CACHE_PREFIX = "fare:";
    public static final String SURGE_PREFIX = "surge:";
    public static final String ACTIVE_RIDE_PREFIX = "ride:active:";
    
    public static String driverLocation(String driverId) {
        return DRIVER_LOCATION_PREFIX + driverId;
    }
    
    public static String driversByH3(String h3Index) {
        return DRIVER_H3_PREFIX + h3Index;
    }
    
    public static String rideLock(String rideId) {
        return RIDE_LOCK_PREFIX + rideId;
    }
    
    public static String fareCache(String fareId) {
        return FARE_CACHE_PREFIX + fareId;
    }
    
    public static String surge(String h3Index) {
        return SURGE_PREFIX + h3Index;
    }
    
    public static String activeRide(String driverId) {
        return ACTIVE_RIDE_PREFIX + driverId;
    }
}
