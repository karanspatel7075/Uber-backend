package com.example.Navio.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long riderId; // Basically User
    private Long driverId;
    private String pickUpLocation;
    private String dropLocation;

    private double pickUpLatitude;
    private double pickUpLongitude;
    private double dropLatitude;
    private double dropLongitude;

    private LocalDateTime requestedTime;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String status; // Requested, Accepted, Ongoing, Completed
    private Double fare;
    private Double rating;
}
