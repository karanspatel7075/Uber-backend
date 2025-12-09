package com.example.Navio.controller;

import com.example.Navio.Payment.StripeResponse;
import com.example.Navio.Payment.StripeService;
import com.example.Navio.model.Ride;
import com.example.Navio.model.User;
import com.example.Navio.repository.RideRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ride")
public class RidePaymentController {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private StripeService stripeService;


    // ----------------------------------------
    // 1️⃣ Online Payment (Stripe)
    // ----------------------------------------

    @PostMapping("/{rideId}/pay-online")
    public String payOnline(@PathVariable Long rideId, Model model, RedirectAttributes redirectAttributes) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if(!"Completed".equals(ride.getStatus())) {
            redirectAttributes.addFlashAttribute("error", "Ride is not completed yet.");
            return "redirect:/rider/rideDetails/" + rideId;
        }

        // Call Stripe Service to create session
        StripeResponse stripeResponse = stripeService.checkoutFare(ride.getFare(), rideId);

        if("success".equals(stripeResponse.getStatus())) {
            // Redirect to Stripe Checkout | Space after redirect: breaks the redirect
            return "redirect:" + stripeResponse.getSessionUrl();
        } else {
            redirectAttributes.addFlashAttribute("error", stripeResponse.getMessage());
            return "redirect:/rider/rideDetails/" + rideId;
        }
    }

    // ----------------------------------------
    // 2️⃣ Cash Payment
    // ----------------------------------------

    @PostMapping("/{rideId}/pay-cash")
    public String payCash(@PathVariable Long rideId, RedirectAttributes redirectAttributes) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if(!"Completed".equals(ride.getStatus())) {
            redirectAttributes.addFlashAttribute("error", "Ride is not completed yet.");
            return "redirect:/rider/rideDetails/" + rideId;
        }

        // Mark the ride as paid with Cash
        ride.setPaymentMethod("Cash");
        rideRepository.save(ride);

        redirectAttributes.addFlashAttribute("message", "Cash payment marked successfully!");
        return "redirect:/rider/dashboard";
    }

    // Optional: Success callback from Stripe
    @GetMapping("/success")
    public String loadSuccess(@RequestParam("session_id") String sessionId, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        Ride ride = stripeService.getRideFromSession(sessionId);

        if (ride != null) {
            ride.setPaymentMethod("Online");
            ride.setStatus("Paid");
            rideRepository.save(ride);
        }
        redirectAttributes.addFlashAttribute("message", "Payment completed successfully!");
        return "redirect:/rider/dashboard";
    }

    @GetMapping("/paymentFailed")
    public String failedSuccess(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Payment was canceled.");
        return "redirect:/rider/dashboard";
    }
}
