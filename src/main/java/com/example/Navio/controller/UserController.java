package com.example.Navio.controller;

import com.example.Navio.auth.*;
import com.example.Navio.dto.UpdateUserDto;
import com.example.Navio.model.Ride;
import com.example.Navio.model.User;
import com.example.Navio.model.enums.Role;
import com.example.Navio.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/uber")
public class UserController {

    private final AuthService authService;
    private final AuthTokenGen authTokenGen;
    private final UserRepository userRepository;

    public UserController(AuthService authService, AuthTokenGen authTokenGen, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.authTokenGen = authTokenGen;
        this.userRepository = userRepository;
    }

    @GetMapping("")
    public String homePage(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("jwtToken");
        if (token != null) {
            String email = authTokenGen.getUsernameFromToken(token);
            User user = userRepository.findByEmail(email).orElse(null);
            model.addAttribute("user", user);
        }
        return "/index";
    }

//    @ModelAttribute
//    public void populateUser(Principal principal, Model model) {
//        if(principal != null) {
//            String email = principal.getName();
//            User user = userRepository.findByEmail(email).orElse(null);
//            model.addAttribute("user", user);
//        }
//    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("userForm", new LoginRequestDto());
        return "/login"; // points to templates/user/login.html
    }

    @GetMapping("/about")
    public String about() {
        return "/about";
    }

    @PostMapping("/login")
    public String login2(@RequestParam String email, @RequestParam String password,
                         Model model, HttpServletRequest request) {
        try {
            LoginResponseDto responseDto = authService.login(new LoginRequestDto(email, password));

            // Store JWT in session
            request.getSession().setAttribute("jwtToken", responseDto.getToken());
            request.getSession().setAttribute("userId", responseDto.getId());

            return "redirect:/uber"; // will call your @GetMapping("") -> homePage()
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        // Invalidate the current session — clears all session data (like jwtToken, userId, etc.)
        request.getSession().invalidate();

        // Redirect to homepage page
        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userForm", new RegisterRequestDto()); // important!
        return "/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("userForm") RegisterRequestDto userForm, Model model) {
        try {
            authService.signup(userForm); // Saves user in DB
            System.out.println("Image is saved here");
            model.addAttribute("success", "User successfully registered. Please login!");
            return "redirect:/uber/login"; // ✅ redirect to login page
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "/register";
        }
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("jwtToken");
        if (token == null) {
            return "redirect:/uber/login"; // force login
        }

        String username = authTokenGen.getUsernameFromToken(token);

        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("token", token);
        model.addAttribute("user", user);
        return "user/profile"; // now Thymeleaf page has access to JWT
    }

    @GetMapping("/loadProfile/{id}")
    public String loadProfile(@PathVariable long id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        model.addAttribute("user", user);
        return "user/updateProfile";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@ModelAttribute UpdateUserDto user, Model model, @RequestParam("img") MultipartFile imgFile) {
        User existingUser = userRepository.findById(user.getId()).orElse(null);

        assert existingUser != null;
        existingUser.setEmail(user.getEmail());
        existingUser.setName(user.getName());
        existingUser.setPhone(user.getPhone());
        existingUser.setRole(Role.valueOf(user.getRole()));

        // Handle file upload
        if(imgFile != null && !imgFile.isEmpty()) {
            try {
                String location = "C:\\Users\\KARAN PATEL\\Desktop\\E-commerce\\Uber";
                File dir = new File(location);
                if(!dir.exists()) dir.mkdirs();

                String filename = System.currentTimeMillis() + "_" + imgFile.getOriginalFilename();
                File destination = new File(dir, filename);
                imgFile.transferTo(destination);

                // Save filename (or relative path) in DB
                existingUser.setImg(filename);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }

        userRepository.save(existingUser);
        model.addAttribute("user", existingUser);

        return "redirect:/uber/profile";    // ✅ redirect after post (Post/Redirect/Get pattern)
    }

    @GetMapping("/getMap")
    public String getMapping(@ModelAttribute ("location") Ride location, Model model) {
        model.addAttribute("pickup", location.getPickUpLocation());
        model.addAttribute("drop", location.getDropLocation());
        return "/map";
    }


}
