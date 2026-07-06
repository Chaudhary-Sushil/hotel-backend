package com.example.Hotel_Management_System.repository;

import com.example.Hotel_Management_System.entity.Booking;
import com.example.Hotel_Management_System.entity.Guest;
import com.example.Hotel_Management_System.entity.Payment;
import com.example.Hotel_Management_System.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // find payment for a specific booking
    Optional<Payment> findByBooking(Booking booking);

    // find all payments by status
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    // find all payments for a guest
    List<Payment> findByBooking_Guest(Guest guest);
}