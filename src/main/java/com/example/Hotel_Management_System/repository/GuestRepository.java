package com.example.Hotel_Management_System.repository;

import com.example.Hotel_Management_System.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    Optional<Guest> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Guest> findByPassportNumber(String passportNumber);
    Optional<Guest> findByCitizenshipNumber(String citizenshipNumber);
}
