package com.example.Hotel_Management_System.service;

import com.example.Hotel_Management_System.dto.PaymentRequest;
import com.example.Hotel_Management_System.dto.PaymentResponse;
import com.example.Hotel_Management_System.entity.*;
import com.example.Hotel_Management_System.exception.*;
import com.example.Hotel_Management_System.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final GuestRepository guestRepository;

    // ─── CREATE PAYMENT ───────────────────────────────────────────
    public PaymentResponse createPayment(PaymentRequest request) {

        // 1. find booking
        var booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking not found: " + request.getBookingId()
                ));

        // 2. check booking is confirmed or checked in
        if (booking.getBookedStatus() != BookedStatus.CONFIRMED &&
                booking.getBookedStatus() != BookedStatus.CHECKED_IN) {
            throw new RoomNotAvailableException(
                    "Payment can only be created for CONFIRMED or CHECKED_IN bookings"
            );
        }

        // 3. check payment doesn't already exist
        if (paymentRepository.findByBooking(booking).isPresent()) {
            throw new RoomNotAvailableException(
                    "Payment already exists for this booking"
            );
        }

        // 4. calculate amount (nights × price)
        long nights = ChronoUnit.DAYS.between(
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );
        double amount = nights * booking.getRoom().getPrice();

        // 5. create payment
        var payment = Payment.builder()
                .booking(booking)
                .amount(amount)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .dueDate(LocalDateTime.now().plusDays(1))  // due tomorrow
                .build();

        paymentRepository.save(payment);

        return buildPaymentResponse(payment);
    }

    // ─── PROCESS PAYMENT ──────────────────────────────────────────
    public PaymentResponse processPayment(Long paymentId) {

        // 1. find payment
        var payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found: " + paymentId
                ));

        // 2. check not already paid
        if (payment.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RoomNotAvailableException(
                    "Payment already processed"
            );
        }

        // 3. mark as paid
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaidDate(LocalDateTime.now());
        paymentRepository.save(payment);

        return buildPaymentResponse(payment);
    }

    // ─── GET ALL PAYMENTS ─────────────────────────────────────────
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::buildPaymentResponse)
                .collect(Collectors.toList());
    }

    // ─── GET MY PAYMENTS ──────────────────────────────────────────
    public List<PaymentResponse> getMyPayments(String email) {

        var guest = guestRepository.findByEmail(email)
                .orElseThrow(() -> new GuestNotFoundException(
                        "Guest not found: " + email
                ));

        return paymentRepository.findByBooking_Guest(guest)
                .stream()
                .map(this::buildPaymentResponse)
                .collect(Collectors.toList());
    }

    // ─── GET PAYMENT BY BOOKING ───────────────────────────────────
    public PaymentResponse getPaymentByBooking(Long bookingId) {

        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking not found: " + bookingId
                ));

        var payment = paymentRepository.findByBooking(booking)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found for booking: " + bookingId
                ));

        return buildPaymentResponse(payment);
    }

    // ─── HELPER METHOD ────────────────────────────────────────────
    private PaymentResponse buildPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .guestId(payment.getBooking().getGuest().getId())
                .guestName(payment.getBooking().getGuest().getFirstName()
                        + " " + payment.getBooking().getGuest().getLastName())
                .bookingId(payment.getBooking().getId())
                .checkInDate(payment.getBooking().getCheckInDate())
                .checkOutDate(payment.getBooking().getCheckOutDate())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .paidDate(payment.getPaidDate())
                .build();
    }
}