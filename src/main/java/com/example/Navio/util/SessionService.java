package com.example.Navio.util;

import com.example.Navio.auth.AuthTokenGen;
import com.example.Navio.model.User;
import com.example.Navio.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    @Autowired
    private AuthTokenGen authTokenGen;

    @Autowired
    private UserRepository userRepository;

    /**
     * Extracts the currently logged-in user from the session token.
     */
    public User getLoggedInUser(HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("jwtToken");

        if (token == null) {
            throw new RuntimeException("No active session found. Please log in.");
        }

        String email = authTokenGen.getUsernameFromToken(token);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
}

// Like this in all controller : User user = sessionService.getLoggedInUser(request);
//       ✅ This service safely handles session and user lookup.
//       ✅ You can reuse it in Rider, Driver, Admin, or Auth controllers.