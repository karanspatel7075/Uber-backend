package com.example.Navio.service;

import com.example.Navio.auth.AuthTokenGen;
import com.example.Navio.dto.DriverRequestDto;
import com.example.Navio.model.Driver;
import com.example.Navio.model.Ride;
import com.example.Navio.model.User;
import com.example.Navio.model.Wallet;
import com.example.Navio.model.enums.Role;
import com.example.Navio.repository.DriverRepository;
import com.example.Navio.repository.RideRepository;
import com.example.Navio.repository.UserRepository;
import com.example.Navio.repository.WalletRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
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

    @Autowired
    private WalletRepository walletRepository;

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

    public Driver applyForDriver(DriverRequestDto driverDto, User user) {
        Driver driver = new Driver();
//        driver.setUserId(user.getId());
        driver.setUser(user); // link the user entity directly
        driver.setCurrentLocation(driverDto.getCurrentLocation());
        driver.setVehicleId(driverDto.getVehicleId());
        driver.setStatus("Pending");
        driver.setPhoneNumber(user.getPhone());
        driver.setName(user.getName());
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
//        User user = userRepository.findById(driver.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        User user = driver.getUser(); // directly get the linked user
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
            ride.setDriverId(driverId);
            rideRepository.save(ride);
            return "Ride is accepted Successfully";
        }

        return "Ride not found!";
    }

    public String startRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
        ride.setStatus("Ongoing");
        ride.setStartTime(LocalDateTime.now());
        rideRepository.save(ride);
        return "Ride started successfully";
    }

    public String endRide(Long rideId, double distanceCovered) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
        ride.setStatus("Completed");
        ride.setEndTime(LocalDateTime.now());

        double fare = calculateFare(distanceCovered);

        //update wallet
        updateWallet(ride.getRiderId(), ride.getDriverId(), fare);

        rideRepository.save(ride);
        return "Ride completed successfully! Fare: ₹" + fare;
    }

    public double calculateFare(double distanceCovered) {
        double baseFare = 40.00;
        double perKmRate = 15.00;
        return baseFare + (perKmRate * distanceCovered);
    }

    private void updateWallet(Long riderId, Long driverId,double fare) {
        User rider = userRepository.findById(riderId).orElseThrow(() -> new RuntimeException("Rider not found"));
        Driver driver = driverRepository.findById(driverId).orElseThrow(() -> new RuntimeException("Driver not found"));

        Wallet riderWallet = rider.getWallet();
        Wallet driverWallet = driver.getUser().getWallet(); // because Driver has User reference

        if (riderWallet == null || driverWallet == null) {
            throw new RuntimeException("Wallet not found for rider or driver");
        }

        // Update balances
        riderWallet.setBalance(riderWallet.getBalance() - fare);
        driverWallet.setBalance(driverWallet.getBalance() + fare);

        // Optionally add transaction history
        riderWallet.getTransactionHistory().add("Debited ₹" + fare + " for ride payment.");
        driverWallet.getTransactionHistory().add("Credited ₹" + fare + " from completed ride.");

        // Save wallets
        walletRepository.save(riderWallet);
        walletRepository.save(driverWallet);
    }

    public List<Ride> getHistory(Long riderId) {
        return rideRepository.findByRiderId(riderId);
    }


    public void updateDriverLocation(Long driverId, String location) {
        Driver driver = driverRepository.findById(driverId).orElseThrow();
        double[] coords = getCoordinates(location);
        driver.setLatitude(coords[0]);
        driver.setLongitude(coords[1]);
        driver.setCurrentLocation(location);
        driverRepository.save(driver);
    }
}
