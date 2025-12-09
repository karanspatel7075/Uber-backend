package com.example.Navio.Payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StripeResponse {

    private String status;
    private String message;
    private Long rideId;
    private String sessionId;
    private String sessionUrl;

}
