package com.example.Navio.config;

import com.example.Navio.dto.RideRequestDto;
import com.example.Navio.interfaces.DriverMatchingStrategy;
import com.example.Navio.model.Driver;
import com.example.Navio.repository.DriverRepository;
import com.example.Navio.service.GeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class NearestDriverStrategy implements DriverMatchingStrategy {

    @Autowired
    private GeoService geoService;

    @Autowired
    private DriverRepository driverRepository;

//    GeoHash → Fast filtering
//    Haversine → Exact distance
//    We will add the Geohashing login afterward at large scale data

    @Override
    public List<Driver> findDriver(List<Driver> availableDriver, RideRequestDto rideRequestDto) {
        if (availableDriver == null || availableDriver.isEmpty()) {
            System.out.println("⚠️ No available drivers found in DB.");
            return Collections.emptyList();
        }

        // Step 1: Convert pickup city name to coordinates
        double[] pickupCoords = getCoordinates(rideRequestDto.getPickUpLocation());
        double lat = pickupCoords[0];
        double lon = pickupCoords[1];

        System.out.println("📍 Pickup coordinates: " + lat + ", " + lon);

        // Step 2: Ask Redis to find drivers within 5km radius
        List<String> nearbyDriverIds = geoService.findNearByDrivers(lat, lon, 5.0);

        // Step 3: If Redis returns nothing, fallback to all available drivers
        if(nearbyDriverIds.isEmpty()) {
            return  driverRepository.findByAvailableTrue();
        }

        // Step 4: Convert IDs → Full Driver entities from DB
        return driverRepository.findAllById(
                nearbyDriverIds.stream().map(Long::parseLong).toList()
        );
    }

    private double[] getCoordinates(String city) {
        return switch (city.toLowerCase()) {
            case "vapi" -> new double[]{20.3710, 72.9043};
            case "daman" -> new double[]{20.4143, 72.8324};
            case "surat" -> new double[]{21.1702, 72.8311};
            case "valsad" -> new double[]{20.6107, 72.9342};
            case "navsari" -> new double[]{20.9490, 72.9243};
            default -> new double[]{0.0, 0.0}; // fallback if not found
        };
    }

    public double distance(String loc1, String loc2) {
        return Math.abs(loc1.hashCode() - loc2.hashCode()) % 100; // Dummy distance calculation.
    }

    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth Radius
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }

}
