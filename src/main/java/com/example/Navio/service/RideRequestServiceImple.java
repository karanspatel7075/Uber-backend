package com.example.Navio.service;

import com.example.Navio.config.NearestDriverStrategy;
import com.example.Navio.dto.RideRequestDto;
import com.example.Navio.interfaces.DriverMatchingStrategy;
import com.example.Navio.model.Driver;
import com.example.Navio.model.Ride;
import com.example.Navio.model.User;
import com.example.Navio.repository.DriverRepository;
import com.example.Navio.repository.RideRepository;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class RideRequestServiceImple {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverMatchingStrategy driverMatchingStrategy;

    @Autowired
    private  DriverServiceImple driverServiceImple;

    @Autowired
    private NotificationServiceImple notificationServiceImple;

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

//    1. Request Ride
    public void requestRide(Long driverId, RideRequestDto dto, User user) throws MessagingException, UnsupportedEncodingException {
        Ride ride = new Ride();
        Driver driver = driverRepository.findById(driverId).orElseThrow();
        ride.setDriverId(driverId);
        ride.setRiderId(user.getId());
        ride.setPickUpLocation(dto.getPickUpLocation());
        ride.setDropLocation(dto.getDropLocation());
        ride.setRequestedTime(LocalDateTime.now());

        double[] pickupCoords = getCoordinates(dto.getPickUpLocation());
        double[] dropCoords = getCoordinates(dto.getDropLocation());

        ride.setPickUpLatitude(pickupCoords[0]);
        ride.setPickUpLongitude(pickupCoords[1]);
        ride.setDropLatitude(dropCoords[0]);
        ride.setDropLongitude(dropCoords[1]);

        ride.setStatus("Requested");
        ride.setFare(driverServiceImple.calculateFare(50));
        System.out.println("Rider: " + user.getName());
        System.out.println("Driver: " + driver.getName());
        System.out.println("Pickup: " + dto.getPickUpLocation());
        System.out.println("Drop: " + dto.getDropLocation());
        log.info("Ride requested by {}", user.getEmail());
        System.out.println("Driver Phone: " + user.getPhone());

//        try {
//            notificationServiceImple.sendConfirmationMail(driver, dto, user, ride.getFare());
//            System.out.println("The mail has been sent to respective User");
//        }
//        catch (Exception e) {
//            System.out.println(("Failed to send the email " + e.getMessage()));
//            e.printStackTrace();
//        }

        driver.setAvailable(false);
        driverRepository.save(driver);
        rideRepository.save(ride);
    }

    public String cancelRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElse(null);
        assert ride != null;
        ride.setStatus("Cancelled");
        rideRepository.save(ride);
        return  "Ride cancelled Successfully";
    }

    public List<Ride> getAllRiders(Long riderId) {
        return rideRepository.findByRiderId(riderId);
    }

    public String rateDriver(Long rideId, Double rating) {
        Ride ride = rideRepository.findById(rideId).orElse(null);
        if(ride != null) {
//            ride.setStatus("Completed");  This Status update has to be done by Driver
            ride.setRating(rating);
            rideRepository.save(ride);
            return "Driver rated successfully!";
        }
        return "Ride not found";
    }


}
