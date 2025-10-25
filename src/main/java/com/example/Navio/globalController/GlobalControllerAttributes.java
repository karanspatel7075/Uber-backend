package com.example.Navio.globalController;

import com.example.Navio.auth.AuthTokenGen;
import com.example.Navio.model.User;
import com.example.Navio.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalControllerAttributes {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthTokenGen authTokenGen;

    @ModelAttribute
    public void addUserToModel(HttpServletRequest request, Model model) {
        try {
            String token = (String) request.getSession().getAttribute("jwtToken");
            if (token != null) {
                String email = authTokenGen.getUsernameFromToken(token);
                User user = userRepository.findByEmail(email).orElse(null);
                model.addAttribute("user", user);
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not load user in GlobalControllerAttributes: " + e.getMessage());
        }
    }
}
