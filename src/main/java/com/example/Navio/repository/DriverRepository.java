package com.example.Navio.repository;

import com.example.Navio.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Driver findByUserId(Long userId);
    List<Driver> findByStatus(String status);
    List<Driver> findByAvailableTrue();
}
