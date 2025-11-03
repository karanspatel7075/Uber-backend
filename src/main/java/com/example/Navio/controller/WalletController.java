package com.example.Navio.controller;

import com.example.Navio.auth.AuthTokenGen;
import com.example.Navio.model.User;
import com.example.Navio.model.Wallet;
import com.example.Navio.repository.UserRepository;
import com.example.Navio.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/wallet")
public class WalletController {

//    Your WalletController provides manual wallet management — like viewing balance, adding funds, or debiting money by user action.

    private final WalletService walletService;
    private final UserRepository userRepository;
    private final AuthTokenGen authTokenGen;

    public WalletController(WalletService walletService, UserRepository userRepository, AuthTokenGen authTokenGen) {
        this.walletService = walletService;
        this.userRepository = userRepository;
        this.authTokenGen = authTokenGen;
    }

    @GetMapping("/details")
    public String getWalletDetails(Model model, HttpServletRequest request) {
        // Get JWT token from session
        String token = (String) request.getSession().getAttribute("jwtToken");
        if (token == null) {
            model.addAttribute("error", "User not logged in");
            return "wallet-details";
        }

        // Get user email from token
        String email = authTokenGen.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email).orElse(null);
        model.addAttribute("user", user);

        System.out.println(user);

        if (user == null) {
            model.addAttribute("error", "User not found or not logged in!");
            return "wallet-details";
        }

        Wallet wallet = walletService.getWalletDetails(user.getId());
        model.addAttribute("details", wallet);
        return "wallet-details";
    }

    @PostMapping("/addFunds")
    public String addFunds(HttpServletRequest request, @RequestParam Double amount, Model model) {
        // Get JWT token from session
        String token = (String) request.getSession().getAttribute("jwtToken");
        if (token == null) {
            model.addAttribute("error", "User not logged in");
            return "wallet-details";
        }

        // Get user email from token
        String email = authTokenGen.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email).orElse(null);
        model.addAttribute("user", user);

        if (user == null) {
            model.addAttribute("error", "User not found or not logged in!");
            return "wallet-details";
        }
        Wallet addFund = walletService.addFunds(user.getId(), amount);
        model.addAttribute("details", addFund);
        model.addAttribute("message", "Funds added successfully!");
        return "wallet-details";
    }

    @PostMapping("/debitFunds")
    public String debtFunds(HttpServletRequest request, @RequestParam Double amount, Model model) {
        // Get JWT token from session
        String token = (String) request.getSession().getAttribute("jwtToken");
        if (token == null) {
            model.addAttribute("error", "User not logged in");
            return "wallet-details";
        }

        // Get user email from token
        String email = authTokenGen.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email).orElse(null);
        model.addAttribute("user", user);

        if (user == null) {
            model.addAttribute("error", "User not found or not logged in!");
            return "wallet-details";
        }

        try {
            Wallet debtFund = walletService.debtFunds(user.getId(), amount);
            model.addAttribute("details", debtFund);
            model.addAttribute("message", "Funds debited successfully!");
        }
        catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "wallet-details";
    }
}
