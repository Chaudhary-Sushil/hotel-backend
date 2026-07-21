package com.example.Hotel_Management_System.controller;

import com.example.Hotel_Management_System.dto.PaymentRequest;
import com.example.Hotel_Management_System.dto.PaymentResponse;
import com.example.Hotel_Management_System.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ─── CREATE PAYMENT ───────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RECEPTIONIST')")
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPayment(request));
    }

    // ─── PROCESS PAYMENT ──────────────────────────────────────────
    @PatchMapping("/{paymentId}/process")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RECEPTIONIST')")
    public ResponseEntity<PaymentResponse> processPayment(
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.processPayment(paymentId));
    }

    // ─── GET ALL PAYMENTS ─────────────────────────────────────────
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RECEPTIONIST')")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // ─── REFUND PAYMENT ─────────────────────────────────────────────
    @PatchMapping("/{paymentId}/refund")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or (hasAuthority('ROLE_RECEPTIONIST') and hasAuthority('MANAGE_PAYMENTS'))")
    public ResponseEntity<PaymentResponse> refundPayment(
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.refundPayment(paymentId));
    }


    // ─── GET MY PAYMENTS ──────────────────────────────────────────
    @GetMapping("/my-payments")
    @PreAuthorize("hasAuthority('ROLE_GUEST')")
    public ResponseEntity<List<PaymentResponse>> getMyPayments(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                paymentService.getMyPayments(userDetails.getUsername())
        );
    }

    // ─── GET PAYMENT BY BOOKING ───────────────────────────────────
    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RECEPTIONIST', 'ROLE_GUEST')")
    public ResponseEntity<PaymentResponse> getPaymentByBooking(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentByBooking(bookingId));
    }

    // ─── GENERATE QR CODE (PNG) ───────────────────────────────────
    @GetMapping(value = "/{bookingId}/qr", produces = org.springframework.http.MediaType.IMAGE_PNG_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RECEPTIONIST', 'ROLE_GUEST')")
    public ResponseEntity<byte[]> getPaymentQr(@PathVariable Long bookingId) {
        byte[] qrBytes = paymentService.getPaymentQrCode(bookingId);
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.IMAGE_PNG)
                .body(qrBytes);
    }

    // ─── MOCK GATEWAY Webhook Callback ─────────────────────────────
    // Simulates an external payment gateway webhook callback that confirms the payment.
    @PostMapping("/{bookingId}/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.confirmPayment(bookingId));
    }

    // ─── SCAN QR Webhook / Confirm by QR Token ──────────────────────
    @GetMapping("/pay/confirm/{token}")
    public ResponseEntity<String> confirmPaymentByTokenGet(@PathVariable String token) {
        paymentService.confirmPaymentByToken(token);
        return ResponseEntity.ok("<h3>Payment Confirmed Successfully!</h3><p>You can close this tab and return to the application.</p>");
    }

    @PostMapping("/pay/confirm/{token}")
    public ResponseEntity<PaymentResponse> confirmPaymentByTokenPost(@PathVariable String token) {
        return ResponseEntity.ok(paymentService.confirmPaymentByToken(token));
    }
}