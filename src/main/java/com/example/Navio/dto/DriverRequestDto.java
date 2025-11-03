package com.example.Navio.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DriverRequestDto {
    private String currentLocation;
    private String vehicleId;
    private String latitude;
    private String longitude;
}

// That’s a bit too detailed for an “apply for driver” form.
// When someone applies to become a driver, they shouldn’t set availability, rating, or status — these are system-managed fields.