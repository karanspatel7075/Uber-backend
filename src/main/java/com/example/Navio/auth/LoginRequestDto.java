package com.example.Navio.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    private String email ;
    private String password;

//    💡 In short:
//    LoginRequestDto → Request body for login
//    LoginResponseDto → Response body containing the JWT
}
