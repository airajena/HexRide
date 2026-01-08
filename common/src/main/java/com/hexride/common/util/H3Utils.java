package com.hexride.common.util;

import com.uber.h3core.H3Core;
import com.uber.h3core.util.LatLng;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class H3Utils {
    
    private static final H3Core h3;
    private static final int DEFAULT_RESOLUTION = 9;
    
    static {
        try {
            h3 = H3Core.newInstance();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize H3", e);
        }
    }
    
    private H3Utils() {}
    
    public static String coordsToH3(double latitude, double longitude) {
        return h3.latLngToCellAddress(latitude, longitude, DEFAULT_RESOLUTION);
    }
    
    public static String coordsToH3(double latitude, double longitude, int resolution) {
        return h3.latLngToCellAddress(latitude, longitude, resolution);
    }
    
    public static Set<String> getKRing(String h3Index, int k) {
        return new HashSet<>(h3.gridDisk(h3Index, k));
    }
    
    public static LatLng h3ToLatLng(String h3Index) {
        return h3.cellToLatLng(h3Index);
    }
    
    public static double getDistanceKm(String h3Index1, String h3Index2) {
        LatLng center1 = h3.cellToLatLng(h3Index1);
        LatLng center2 = h3.cellToLatLng(h3Index2);
        return haversineDistance(center1.lat, center1.lng, center2.lat, center2.lng);
    }
    
    public static double getDistanceKm(double lat1, double lng1, double lat2, double lng2) {
        return haversineDistance(lat1, lng1, lat2, lng2);
    }
    
    private static double haversineDistance(double lat1, double lng1, double lat2, double lng2) {
        final int EARTH_RADIUS_KM = 6371;
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
    
    public static boolean isValidH3Index(String h3Index) {
        try {
            return h3.isValidCell(h3Index);
        } catch (Exception e) {
            return false;
        }
    }
    
    public static int getResolution(String h3Index) {
        return h3.getResolution(h3Index);
    }
    
    public static List<String> getNeighbors(String h3Index) {
        return h3.gridDisk(h3Index, 1);
    }
}
