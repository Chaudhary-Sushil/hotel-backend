package com.example.Hotel_Management_System.repository;

import com.example.Hotel_Management_System.dto.RevenueDailyDto;
import com.example.Hotel_Management_System.entity.Booking;
import com.example.Hotel_Management_System.entity.Guest;
import com.example.Hotel_Management_System.entity.Payment;
import com.example.Hotel_Management_System.entity.PaymentStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // find payment for a specific booking
    Optional<Payment> findByBooking(Booking booking);

    // find payment by QR token
    Optional<Payment> findByQrToken(String qrToken);

    // find all payments by status
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    // find all payments for a guest
    List<Payment> findByBooking_Guest(Guest guest);

    // ─── DASHBOARD AGGREGATE QUERIES ────────────────────────────────

    /**
     * Daily revenue grouped by the date portion of paidDate.
     * Only includes PAID payments from the given since timestamp onwards.
     * Uses FUNCTION('DATE', ...) which works with MySQL's DATE() function via JPA.
     */
    @Query("SELECT new com.example.Hotel_Management_System.dto.RevenueDailyDto(" +
           "CAST(p.paidDate AS LocalDate), SUM(p.amount)) " +
           "FROM Payment p WHERE p.paymentStatus = 'PAID' " +
           "AND p.paidDate >= :since " +
           "GROUP BY CAST(p.paidDate AS LocalDate) " +
           "ORDER BY CAST(p.paidDate AS LocalDate) ASC")
    List<RevenueDailyDto> getDailyRevenue(@Param("since") LocalDateTime since);

    /** Latest N payments ordered by createdAt desc — for recent activity feed */
    @Query("SELECT p FROM Payment p ORDER BY p.createdAt DESC")
    List<Payment> findLatestPayments(Pageable pageable);
}