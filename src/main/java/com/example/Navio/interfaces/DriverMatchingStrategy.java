package com.example.Navio.interfaces;

import com.example.Navio.dto.RideRequestDto;
import com.example.Navio.model.Driver;

import java.util.List;

public interface DriverMatchingStrategy {
    //
    List<Driver> findDriver(List<Driver> availableDriver, RideRequestDto rideRequestDto);
}
