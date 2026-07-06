package com.example.Hotel_Management_System.controller;

import com.example.Hotel_Management_System.dto.*;
import com.example.Hotel_Management_System.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // ─── GUEST OPERATIONS ─────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_GUEST')")
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                bookingService.createBooking(request, userDetails.getUsername())
        );
    }

    @PatchMapping("/{bookingId}/cancel")
    @PreAuthorize("hasAuthority('ROLE_GUEST')")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                bookingService.cancelBooking(bookingId, userDetails.getUsername())
        );
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasAuthority('ROLE_GUEST')")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                bookingService.getMyBookings(userDetails.getUsername())
        );
    }

    // ─── RECEPTIONIST/ADMIN OPERATIONS ───────────────────────────
    @PatchMapping("/{bookingId}/confirm")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RECEPTIONIST')")
    public ResponseEntity<BookingResponse> confirmBooking(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.confirmBooking(bookingId));
    }

    @PatchMapping("/{bookingId}/checkin")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RECEPTIONIST')")
    public ResponseEntity<BookingResponse> checkIn(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.checkIn(bookingId));
    }

    @PatchMapping("/{bookingId}/checkout")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RECEPTIONIST')")
    public ResponseEntity<BookingResponse> checkOut(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.checkOut(bookingId));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RECEPTIONIST')")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/{bookingId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RECEPTIONIST')")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBookingById(bookingId));
    }
}