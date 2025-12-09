package com.example.Navio.controller;

import com.example.Navio.auth.AuthTokenGen;
import com.example.Navio.config.NearestDriverStrategy;
import com.example.Navio.dto.RideRequestDto;
import com.example.Navio.interfaces.DriverMatchingStrategy;
import com.example.Navio.model.Driver;
import com.example.Navio.model.Ride;
import com.example.Navio.model.User;
import com.example.Navio.repository.DriverRepository;
import com.example.Navio.repository.RideRepository;
import com.example.Navio.repository.UserRepository;
import com.example.Navio.service.DriverServiceImple;
import com.example.Navio.service.GeoService;
import com.example.Navio.service.RideRequestServiceImple;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
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
    public RideRepository rideRepository;

    @Autowired
    private DriverMatchingStrategy driverMatchingStrategy;

    @Autowired
    private NearestDriverStrategy strategy;

    @Autowired
    private GeoService geoService;

    @Autowired
    private DriverServiceImple driverServiceImple;

    private static final Logger logger = LoggerFactory.getLogger(RideController.class);

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
        try {
            String token = (String) request.getSession().getAttribute("jwtToken");
            String email = authTokenGen.getUsernameFromToken(token);
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("No user found"));



            // Saving the ride request
            rideRequestServiceImple.requestRide(driverId, dto, user);

            // get latest ride created by this rider
            List<Ride> rides = rideRequestServiceImple.getAllRiders(user.getId());
            Ride latestRide = rides.stream()
                    .max(Comparator.comparing(Ride::getRequestedTime))
                    .orElseThrow();

            // get the last one
            Long latestId = latestRide.getId();

            // Find nearby drivers (for notification)
            List<Driver> nearbyDrivers = driverServiceImple.findNearbyDrivers(dto);
            List<Long> driverIds = nearbyDrivers.stream()
                    .map(Driver::getId)
                    .toList();

            System.out.println("Publishing new ride to Redis...");

            // publish the new ride to Redis channel for websocket to broadcast it
            rideRequestServiceImple.publishNewRideToDriver(latestRide, driverIds);

            System.out.println("✅ Redis publish call complete.");

            redirectAttributes.addFlashAttribute("message", "Ride requested successfully ");
//            return "redirect:/rider/dashboard";

            // Redirect rider to the ride page
            return "redirect:/rider/ride/" + latestId;
        } catch (Exception e) {
            e.printStackTrace(); // temp debugging
            redirectAttributes.addFlashAttribute("error", "Something went wrong: " + e.getMessage());
            return "redirect:/rider/dashboard";
        }
    }

    @PostMapping("/findDriver")
    public String findDriver(@ModelAttribute RideRequestDto rideRequestDto, HttpServletRequest request, Model model) {
        String token = (String) request.getSession().getAttribute("jwtToken");
        String email = authTokenGen.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        List<Driver> availableDrivers = driverRepository.findByAvailableTrue();

        List<Driver> nearbyDrivers = driverServiceImple.findNearbyDrivers(rideRequestDto);

        model.addAttribute("dto", rideRequestDto);
        model.addAttribute("drivers", nearbyDrivers);
//        return "rider/selectDriver";
        return "/map";
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

    @GetMapping("/map")
    public String getMapping() {
        return "map";
    }

    @RequestMapping("/favicon.ico")
    @ResponseBody
    public void disableFavicon() {
        // Prevent Spring from interpreting /favicon.ico as rideId
    }

    @GetMapping("/chat")
    public String openChatPage(@RequestParam("rideId") Long rideId,
                               HttpServletRequest request,
                               Model model) {
        String token = (String) request.getSession().getAttribute("jwtToken");
        String email = authTokenGen.getUsernameFromToken(token);
        logger.info("📧 Rider email extracted from token: {}", email);

        Ride ride = rideRepository.findById(rideId).orElseThrow();
        Driver driver = driverRepository.findById(ride.getDriverId()).orElseThrow();
        logger.info("🚗 Ride found: Pickup={}, Drop={}, DriverId={}",
                ride.getPickUpLocation(), ride.getDropLocation(), ride.getDriverId());

        model.addAttribute("driverEmail", driver.getUser().getEmail());
        model.addAttribute("riderEmail", email);
        model.addAttribute("sessionJwt", token); // ⚡ Important for WebSocket


        logger.info("✅ Chat page model prepared successfully.");
        return "chat_page";
    }

    @GetMapping("/ride/{rideId}")
    public String rideDetails(@PathVariable Long rideId, Model model) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("No ride is present"));

        User rider = userRepository.findById(ride.getRiderId())
                        .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("rider", rider);
        model.addAttribute("ride", ride);
        return "rider/rideDetails";
    }

    @GetMapping("/getPayment/{rideId}")
    public String getPaymentMethod(@PathVariable Long rideId, Model model) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("No ride found"));
        model.addAttribute("ride", ride);
        return "payments";
    }




}
