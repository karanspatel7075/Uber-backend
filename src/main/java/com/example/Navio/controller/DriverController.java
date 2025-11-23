package com.example.Navio.controller;

import com.example.Navio.auth.AuthTokenGen;
import com.example.Navio.dto.DriverRequestDto;
import com.example.Navio.model.Driver;
import com.example.Navio.model.Ride;
import com.example.Navio.model.User;
import com.example.Navio.repository.DriverRepository;
import com.example.Navio.repository.RideRepository;
import com.example.Navio.repository.UserRepository;
import com.example.Navio.service.DriverServiceImple;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/driver")
public class DriverController {

    @Autowired
    private AuthTokenGen authTokenGen;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverServiceImple driverServiceImple;

    @Autowired
    private RideRepository rideRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        Driver driver = driverServiceImple.findDriverByUser(request);
        List<Ride> listOfRides = rideRepository.findByStatus("Requested");
        model.addAttribute("rides", listOfRides);
        model.addAttribute("driver", driver);
        return "driver/dashboard"; // driver/dashboard.html
    }

    @GetMapping("/apply")
    public String applyForm(Model model) {
        model.addAttribute("driverRequestDto", new DriverRequestDto());
        return "driver/apply";
    }

    @PostMapping("/applyForm")
    public String applyForDriver(@ModelAttribute("driverRequestDto") DriverRequestDto dto, HttpServletRequest request, Model model) {
        try {
            String token = (String) request.getSession().getAttribute("jwtToken");
            String email = authTokenGen.getUsernameFromToken(token);
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

            driverServiceImple.applyForDriver(dto, user);
            return "redirect:/driver/dashboard";
        } catch (Exception e) {
            // You can log it and show a custom error message on the same page
            model.addAttribute("error", e.getMessage());
            e.printStackTrace();
            return "errorPage"; // You can create errorPage.html or use driver/apply again
        }
    }

    @PostMapping("/acceptRide")
    public String acceptRide(HttpServletRequest request, @RequestParam Long rideId,  RedirectAttributes redirectAttributes) {
        // Get logged-in driver
        String token = (String) request.getSession().getAttribute("jwtToken");
        String email = authTokenGen.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Driver driver = driverRepository.findByUserId(user.getId());

        // Call the service
        String message = driverServiceImple.acceptRide(driver.getId(), rideId);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/driver/ride/" + rideId;
    }

    @GetMapping("/ride/{rideId}")
    public String rideDetails(@PathVariable Long rideId, Model model) {
        Ride ride = rideRepository.findById(rideId).orElse(null);
        model.addAttribute("ride", ride);
        return "driver/rideDetails";
    }

    @PostMapping("/startRide/{rideId}")
    public String startRide(@PathVariable Long rideId, RedirectAttributes redirectAttributes) {
        String message = driverServiceImple.startRide(rideId);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/driver/ride/" + rideId;
    }

    @PostMapping("/endRide/{rideId}")
    public String endRide(@PathVariable Long rideId, @RequestParam Double distance, RedirectAttributes redirectAttributes) {
        String message = driverServiceImple.endRide(rideId, distance);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/driver/ride/" + rideId;
    }

    @PostMapping("/updateLocation")
    public String updateLocation(@RequestParam Long rideId, @RequestParam("dropLocation") String location, RedirectAttributes redirectAttributes) {
        driverServiceImple.updateDriverLocation(rideId, location);
        redirectAttributes.addFlashAttribute("message", "Successfully Updated");
        return "redirect:/driver/dashboard";
    }

    @RequestMapping("/favicon.ico")
    @ResponseBody
    public void disableFavicon() {
        // Prevent Spring from interpreting /favicon.ico as rideId
    }

    @GetMapping("/chat")
    public String openDriverChatPage(@RequestParam("rideId") Long rideId, HttpServletRequest request,
                                     Model model) {
        String token = (String) request.getSession().getAttribute("jwtToken");
        String email = authTokenGen.getUsernameFromToken(token);

        Ride ride = rideRepository.findById(rideId).orElseThrow();
        User rider = userRepository.findById(ride.getRiderId()).orElseThrow(() -> new RuntimeException("Rider not found"));

        model.addAttribute("driverEmail", email);
        model.addAttribute("riderEmail", rider.getEmail());
        model.addAttribute("sessionJwt", token); // ⚡ Important for WebSocket

        return "chat_page";
    }



}
