package com.example.Hotel_Management_System.controller;

import com.example.Hotel_Management_System.dto.*;
import com.example.Hotel_Management_System.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ─── REGISTER GUEST ───────────────────────────────────────────
    @PostMapping("/register/guest")
    public ResponseEntity<AuthResponse> registerGuest(
            @Valid @RequestBody RegisterGuestRequest request) {
        return ResponseEntity.ok(authService.registerGuest(request));
    }

    // ─── REGISTER STAFF ───────────────────────────────────────────
    @PostMapping("/register/staff")
    public ResponseEntity<AuthResponse> registerStaff(
            @Valid @RequestBody RegisterStaffRequest request) {
        return ResponseEntity.ok(authService.registerStaff(request));
    }

    // ─── LOGIN ────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // ─── REFRESH TOKEN ────────────────────────────────────────────
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    // ─── LOGOUT ───────────────────────────────────────────────────
    @PostMapping("/logout")
    public ResponseEntity<Map<String,String>> logout(
            @Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(
                Map.of("message", "Logged out successfully ")
        );
    }


}