package com.example.Navio.controller;

import com.example.Navio.auth.AuthTokenGen;
import com.example.Navio.dto.DriverRequestDto;
import com.example.Navio.model.Driver;
import com.example.Navio.model.User;
import com.example.Navio.repository.UserRepository;
import com.example.Navio.service.DriverServiceImple;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/driver")
public class DriverController {

    @Autowired
    private AuthTokenGen authTokenGen;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverServiceImple driverServiceImple;


    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        Driver driver = driverServiceImple.findDriverByUser(request);
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
        String token = (String) request.getSession().getAttribute("jwtToken");
        String email = authTokenGen.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        driverServiceImple.applyForDriver(dto, user);
        return "redirect:/driver/dashboard";
    }

    @PostMapping("/acceptRide")
    public String acceptRide(@RequestParam Long driverId, @RequestParam Long rideId,  RedirectAttributes redirectAttributes) {
        String message = driverServiceImple.acceptRide(driverId, rideId);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/driver/dashboard";
    }

    @PostMapping("/startRide")
    public String startRide(@RequestParam Long rideId, RedirectAttributes redirectAttributes) {
        String message = driverServiceImple.startRide(rideId);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/driver/dashboard";
    }

    @PostMapping("/endRide")
    public String endRide(@RequestParam Long rideId, @RequestParam Double distance, RedirectAttributes redirectAttributes) {
        String message = driverServiceImple.endRide(rideId, distance);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/driver/dashboard";
    }


    @PostMapping("/updateLocation")
    public String updateLocation(@RequestParam Long driverId, @RequestParam String location, RedirectAttributes redirectAttributes) {
        driverServiceImple.updateDriverLocation(driverId, location);
        redirectAttributes.addFlashAttribute("message", "Successfully Updated");
        return "redirect:/driver/dashboard";
    }
}
