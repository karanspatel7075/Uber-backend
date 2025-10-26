package com.example.Navio.controller;

import com.example.Navio.auth.AuthTokenGen;
import com.example.Navio.config.NearestDriverStrategy;
import com.example.Navio.dto.RideRequestDto;
import com.example.Navio.interfaces.DriverMatchingStrategy;
import com.example.Navio.model.Driver;
import com.example.Navio.model.Ride;
import com.example.Navio.model.User;
import com.example.Navio.repository.DriverRepository;
import com.example.Navio.repository.UserRepository;
import com.example.Navio.service.RideRequestServiceImple;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/rider")
public class RideController {

    @Autowired
    private AuthTokenGen authTokenGen;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideRequestServiceImple rideRequestServiceImple;

    @Autowired
    private DriverMatchingStrategy driverMatchingStrategy;

    @Autowired
    private NearestDriverStrategy strategy;

    @GetMapping("/dashboard")
    public String rideDashboard(HttpServletRequest request, Model model) {
        String token = (String) request.getSession().getAttribute("jwtToken");
        String email = authTokenGen.getUsernameFromToken(token);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        List<Ride> rides = rideRequestServiceImple.getAllRiders(user.getId());
        if (rides == null) {
            rides = new ArrayList<>();
        }

        System.out.println("Fetched rides for user " + user.getId() + " → " + rides.size());
        model.addAttribute("rides", rides);
        System.out.println("✅ Rendering dashboard for: " + user.getEmail());

        return "rider/dashboard";
    }

    @GetMapping("/requestRide")
    public String requestRideForm(Model model) {
        model.addAttribute("rideRequestDto", new RideRequestDto());
        return "rider/requestRide";
    }

    @PostMapping("/selectDriver")
    public String requestRide(@ModelAttribute RideRequestDto dto, @RequestParam Long driverId, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) throws MessagingException, UnsupportedEncodingException {
        String token = (String) request.getSession().getAttribute("jwtToken");
        String email = authTokenGen.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("No user found"));

        rideRequestServiceImple.requestRide(driverId, dto, user);
        redirectAttributes.addFlashAttribute("message", "Ride requested successfully ");
        return "redirect:/rider/dashboard";
    }

    @PostMapping("/findDriver")
    public String findDriver(@ModelAttribute RideRequestDto rideRequestDto, HttpServletRequest request, Model model) {
        String token = (String) request.getSession().getAttribute("jwtToken");
        String email = authTokenGen.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        List<Driver> availableDrivers = driverRepository.findByAvailableTrue();

        List<Driver> nearByDriver = availableDrivers.stream()
                .filter(d -> d.getCurrentLocation().equalsIgnoreCase(rideRequestDto.getPickUpLocation()))
                .toList();

//        for(Driver d : availableDrivers) {
//            double distance = strategy.haversine(riderLat, riderLon, d.getLatitude(), d.getLongitude());
//            if(distance <= 5.0) {
//                nearByDriver.add(d);
//            }
//        }                         // Add this function to find the nearest driver

        if(nearByDriver.isEmpty()) {
            nearByDriver = availableDrivers;
        }

        model.addAttribute("dto", rideRequestDto);
        model.addAttribute("drivers", nearByDriver);
        return "rider/selectDriver";
    }

    @PostMapping("/cancelRide")
    public String cancelRide(@RequestParam Long rideId, RedirectAttributes redirectAttributes) {
        String message = rideRequestServiceImple.cancelRide(rideId);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/rider/dashboard";
    }

    @PostMapping("/rateDriver")
    public String rateDriver(@RequestParam Long rideId, @RequestParam Double rating, RedirectAttributes redirectAttributes) {
        String message = rideRequestServiceImple.rateDriver(rideId, rating);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/rider/dashboard";
    }
}
