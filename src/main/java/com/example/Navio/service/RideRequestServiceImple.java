package com.example.Navio.service;

import com.example.Navio.config.NearestDriverStrategy;
import com.example.Navio.dto.RideRequestDto;
import com.example.Navio.interfaces.DriverMatchingStrategy;
import com.example.Navio.model.Driver;
import com.example.Navio.model.Ride;
import com.example.Navio.model.User;
import com.example.Navio.repository.DriverRepository;
import com.example.Navio.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RideRequestServiceImple {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverMatchingStrategy driverMatchingStrategy;

//    1. Request Ride
    public Driver requestRide(RideRequestDto dto, User user) {
        Ride ride = new Ride();
        ride.setRiderId(user.getId());
        ride.setPickUpLocation(dto.getPickUpLocation());
        ride.setDropLocation(dto.getDropLocation());
        ride.setRequestedTime(LocalDateTime.now());
        ride.setStatus("Requested");

        List<Driver> getAllAvailableDrivers = driverRepository.findByAvailableTrue();

        Driver matchedDriver = driverMatchingStrategy.findDriver(getAllAvailableDrivers, dto);
        if(matchedDriver != null) {
            matchedDriver.setAvailable(false);
            ride.setDriverId(matchedDriver.getId());
            ride.setStatus("Matched");
        }
        else {
            ride.setStatus("Pending");
        }
        rideRepository.save(ride);
        return matchedDriver;
    }

    public String cancelRide(Long riderId) {
        Ride ride = rideRepository.findById(riderId).orElse(null);
        assert ride != null;
        ride.setStatus("Cancelled");
        rideRepository.save(ride);
        return  "Ride cancelled Successfully";
    }

    public List<Ride> getAllRiders(Long riderId) {
        return rideRepository.findByRiderId(riderId);
    }

    public String rateDriver(Long riderId, Double rating) {
        Ride ride = rideRepository.findById(riderId).orElse(null);
        if(ride != null) {
//            ride.setStatus("Completed");  This Status update has to be done by Driver
            ride.setRating(rating);
            rideRepository.save(ride);
            return "Driver rated successfully!";
        }
        return "Ride not found";
    }
}
