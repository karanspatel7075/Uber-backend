package com.example.Navio.model;

import com.example.Navio.model.enums.Role;
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
    private Long userId; // foreign key to User table

    @Column(name = "is_available")
    private boolean available;
    private String currentLocation;
    private Double rating;
    private String vehicleId;
    private String status;

    // Store uploaded file path or URL
    private String img;

    private double longitude;
    private double latitude;

}

