package com.example.Hotel_Management_System.controller;

import com.example.Hotel_Management_System.dto.*;
import com.example.Hotel_Management_System.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StaffController {

    private final AuthService authService;

    // ─── ADMIN: CREATE STAFF ──────────────────────────────────────
    @PostMapping("/admin/staff/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CreateStaffResponse> createStaff(
            @Valid @RequestBody CreateStaffRequest request) {
        return ResponseEntity.ok(authService.createStaff(request));
    }

    // ─── ADMIN: GET ALL STAFF (admin-prefixed) ────────────────────
    @GetMapping("/admin/staff")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getAllStaff() {
        return ResponseEntity.ok(
                authService.getAllStaff()
        );
    }

    // ─── GET ALL STAFF (plain route, matches frontend calls) ──────
    @GetMapping("/staff")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getAllStaffPlain() {
        return ResponseEntity.ok(
                authService.getAllStaff()
        );
    }

    // ─── ADMIN: UPDATE STAFF ───────────────────────────────────────
    @PutMapping("/staff/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<StaffResponse> updateStaff(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStaffRequest request) {
        return ResponseEntity.ok(authService.updateStaff(id, request));
    }

    // ─── ADMIN: DELETE STAFF (hard delete) ─────────────────────────
    @DeleteMapping("/staff/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        authService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }
}