package com.example.Navio.auth;

import com.example.Navio.config.SecurityConfig;
import com.example.Navio.model.Wallet;
import com.example.Navio.model.enums.Role;
import com.example.Navio.model.User;
import com.example.Navio.repository.UserRepository;
import com.example.Navio.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final AuthTokenGen authTokenGen;
    private final SecurityConfig securityConfig;
    private final PasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;

    String uploadDir = "C:\\Users\\KARAN PATEL\\Desktop\\E-commerce\\Uber";

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );

//      Once authentication succeeds, Spring returns an Authentic6+3
//      .ation object that holds: the user’s details (User) the roles/authorities
        User user = (User) authentication.getPrincipal();

        String token = authTokenGen.generateAccessToken(user);

        return new LoginResponseDto(token, user.getId());
    }

    public RegisterResponseDto signup(RegisterRequestDto registerRequestDto) throws IOException {
        if (userRepository.findByEmail(registerRequestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        MultipartFile file = registerRequestDto.getImg();
        File directory = new File(uploadDir);
        if(!directory.exists()) {
            directory.mkdirs();
        }

        String image = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File destination = new File(directory, image);

        file.transferTo(destination);

        // Convert DTO to User entity
        User user = User.builder()
                .name(registerRequestDto.getName())
                .email(registerRequestDto.getEmail())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .phone(registerRequestDto.getPhone())
                .img(image)
                .role(Role.USER)   // default role
                .build();



        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(500.0)
                .build();

        // Link both
        user.setWallet(wallet); // IMPORTANT: sets both sides
        // wallet.setUser(user); <- not needed, handled in setWallet

        // Saving user will automatically save wallet due to CascadeType.ALL
        // Save only the user (CascadeType.ALL will save wallet automatically)
        userRepository.save(user);

        return RegisterResponseDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
