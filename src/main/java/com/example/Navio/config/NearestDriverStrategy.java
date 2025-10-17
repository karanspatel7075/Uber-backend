package com.example.Navio.config;

import com.example.Navio.dto.RideRequestDto;
import com.example.Navio.interfaces.DriverMatchingStrategy;
import com.example.Navio.model.Driver;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class NearestDriverStrategy implements DriverMatchingStrategy {

    @Override
    public Driver findDriver(List<Driver> availableDriver, RideRequestDto rideRequestDto) {
        if(availableDriver.isEmpty()) {
            return  null;
        }

        return availableDriver.stream()
                .min(Comparator.comparing(
                        driver -> distance(driver.getCurrentLocation(), rideRequestDto.getPickUpLocation())
                ))
                .orElse(null);
    }

    public double distance(String loc1, String loc2) {
        return Math.abs(loc1.hashCode() - loc2.hashCode()) % 100; // Dummy distance calculation.
    }
}
