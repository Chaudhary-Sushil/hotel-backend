package com.example.Hotel_Management_System.repository;

import com.example.Hotel_Management_System.entity.Guest;
import com.example.Hotel_Management_System.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    Optional<Staff> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Staff> findByEmployeeId(String employeeId);
}
