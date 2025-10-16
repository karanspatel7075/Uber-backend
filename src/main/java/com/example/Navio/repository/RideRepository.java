package com.example.Navio.repository;

import com.example.Navio.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    //
    List<Ride> findByRiderId(Long riderId);
}
