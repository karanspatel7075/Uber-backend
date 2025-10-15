package com.example.Navio.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {

    private String name;            // Full name
    private String email;
    private String password;
    private Long phone;
    private String role;

    // Use MultipartFile for file uploads
    private MultipartFile img;

}
