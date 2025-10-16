package com.example.Navio.service;

import com.example.Navio.auth.AuthTokenGen;
import com.example.Navio.dto.DriverRequestDto;
import com.example.Navio.model.Driver;
import com.example.Navio.model.Ride;
import com.example.Navio.model.User;
import com.example.Navio.model.enums.Role;
import com.example.Navio.repository.DriverRepository;
import com.example.Navio.repository.RideRepository;
import com.example.Navio.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class DriverServiceImple {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthTokenGen authTokenGen;

    public Driver applyForDriver(DriverRequestDto driverDto, User user) {
        Driver driver = new Driver();
        driver.setUserId(user.getId());
        driver.setCurrentLocation(driverDto.getCurrentLocation());
        driver.setVehicleId(driverDto.getVehicleId());
        driver.setStatus("Pending");
        driver.setRating(0.0);
        driver.setAvailable(false);
        driverRepository.save(driver);
        return driver;
    }

    public List<Driver> getPendingDriver() {
        return driverRepository.findByStatus("Pending");
    }

    public void approveDriver(Long driverId) {
        Driver driver = driverRepository.findById(driverId).orElseThrow(() -> new RuntimeException("Driver not found"));
        driver.setStatus("Approved");
        driver.setAvailable(true);
        driverRepository.save(driver);

//      That’s wrong — driver.getId() is driver table’s ID, not user’s.
        User user = userRepository.findById(driver.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(Role.DRIVER);
        userRepository.save(user);
    }

    public Driver findDriverByUser(HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("jwtToken");
        if (token == null) {
            throw new RuntimeException("No active session or token found");
        }

        String email = authTokenGen.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("No user found"));
        return driverRepository.findByUserId(user.getId());
    }

    public String acceptRide(Long driverId, Long rideId) {
        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        if(rideOpt.isPresent()) {
            Ride ride = rideOpt.get();
            ride.setStatus("Accepted");
            ride.setRiderId(driverId);
            rideRepository.save(ride);
            return "Ride is accepted Successfully";
        }

        return "Ride not found!";
    }

    public String startRide(Long rideId) {
        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        if(rideOpt.isPresent()) {
            Ride ride = rideOpt.get();
            ride.setStatus("Ongoing");
            rideRepository.save(ride);
            return "Ride started!";
        }

        return "Ride not found!";
    }

    public String endRide(Long rideId, double distanceCovered) {
        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        if(rideOpt.isPresent()) {
            Ride ride = rideOpt.get();
            ride.setStatus("Completed");
            double fare = calculateFare(distanceCovered);
            ride.setFare(fare);
            rideRepository.save(ride);
            return "Ride completed! Fare: ₹" + fare;
        }

        return  "Ride not found!";
    }

    public double calculateFare(double distanceCovered) {
        double baseFare = 50.00;
        double perKmRate = 15.00;
        return baseFare + (perKmRate * distanceCovered);
    }

    public String updateDriverLocation(Long driverId, String location) {
        Optional<Driver> driverOpt = driverRepository.findById(driverId);
        if(driverOpt.isPresent()) {
            Driver driver = driverOpt.get();
            driver.setCurrentLocation(location);
            driverRepository.save(driver);
            return "Driver location updated!";
        }

        return "Driver not found!";
    }





}
