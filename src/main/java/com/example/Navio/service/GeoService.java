package com.example.Navio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeoService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

//  Think of it as a folder name inside Redis to hold all driver geo data.
//  GEOHASH key member (this is where the key will be inserted)
    private static final String DRIVER_GEO_KEY = "driver_locations";

    public void addDriverLocation(Long driverId, double latitude, double longitude) {
        redisTemplate.opsForGeo().add(DRIVER_GEO_KEY, new Point(longitude, latitude), driverId.toString());
    }

    public Point getDriverLocations(Long driverId) {
        List<Point> points = redisTemplate.opsForGeo().position(DRIVER_GEO_KEY, driverId.toString());
        return  points != null && !points.isEmpty() ? points.get(0) : null;
    }

//    GEOPOS key member
//    summary: Returns longitude and latitude of members of a geospatial index

    public List<String> findNearByDrivers(double latitude, double longitude, double radiusKm) {
        GeoResults<RedisGeoCommands.GeoLocation<Object>> results =
                redisTemplate.opsForGeo().radius(DRIVER_GEO_KEY, new Circle(new Point(longitude, latitude), new Distance(radiusKm, Metrics.KILOMETERS)));

        if(results == null) {
            return List.of();
        }

        return  results.getContent().stream()
                .map(r -> r.getContent().getName().toString())
                .toList();
    }
}
