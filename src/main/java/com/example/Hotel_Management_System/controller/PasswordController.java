package com.example.Hotel_Management_System.controller;

import com.example.Hotel_Management_System.dto.*;
import com.example.Hotel_Management_System.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    // ─── CHANGE PASSWORD (logged in) ──────────────────────────
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                passwordService.changePassword(
                        userDetails.getUsername(), request
                )
        );
    }

    // ─── FORGOT PASSWORD (public) ─────────────────────────────
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(
                passwordService.forgotPassword(request)
        );
    }

    // ─── RESET PASSWORD (public) ──────────────────────────────
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(
                passwordService.resetPassword(request)
        );
    }
}