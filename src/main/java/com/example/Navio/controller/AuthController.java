package com.example.Navio.controller;

import com.example.Navio.auth.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class AuthController {

//    REST API controller → @RestController for JSON (login/register via API, mobile app, AJAX, Postman).
//    Web controller → @Controller for Thymeleaf pages (login/register pages for browser).

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto) {
        return authService.login(loginRequestDto);
    }

    @PostMapping("/signup")
    public RegisterResponseDto signup(@RequestBody RegisterRequestDto signUpRequestDto) throws IOException {
        return authService.signup(signUpRequestDto);
    }



}
