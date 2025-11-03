package com.example.Navio.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    private Long userId; // foreign key to User table

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "is_available")
    private boolean available;
    private String currentLocation;
    private Double rating = 0.0;
    private String vehicleId;
    private String status;
    private String name;
    private Long phoneNumber;
    private double walletBalance;

    // Store uploaded file path or URL
    private String img;

    private double longitude;
    private double latitude;

    @Column(nullable = false)
    private Double totalRatings = 0.0;

}

