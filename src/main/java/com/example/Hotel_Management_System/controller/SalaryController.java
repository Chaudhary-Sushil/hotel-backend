package com.example.Hotel_Management_System.controller;

import com.example.Hotel_Management_System.dto.SalaryRequest;
import com.example.Hotel_Management_System.dto.SalaryResponse;
import com.example.Hotel_Management_System.entity.SalaryType;
import com.example.Hotel_Management_System.service.SalaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;

    // ─── ADD SALARY ───────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SalaryResponse> addSalary(
            @Valid @RequestBody SalaryRequest request) {
        return ResponseEntity.ok(salaryService.addSalary(request));
    }

    // ─── UPDATE SALARY ────────────────────────────────────────────
    @PutMapping("/{salaryId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SalaryResponse> updateSalary(
            @PathVariable Long salaryId,
            @Valid @RequestBody SalaryRequest request) {
        return ResponseEntity.ok(salaryService.updateSalary(salaryId, request));
    }

    // ─── DELETE SALARY ────────────────────────────────────────────
    @DeleteMapping("/{salaryId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteSalary(
            @PathVariable Long salaryId) {
        return ResponseEntity.ok(salaryService.deleteSalary(salaryId));
    }

    // ─── GET ALL SALARIES ─────────────────────────────────────────
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<SalaryResponse>> getAllSalaries() {
        return ResponseEntity.ok(salaryService.getAllSalaries());
    }

    // ─── GET SALARIES BY STAFF ────────────────────────────────────
    @GetMapping("/staff/{staffId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<SalaryResponse>> getSalariesByStaff(
            @PathVariable Long staffId) {
        return ResponseEntity.ok(salaryService.getSalariesByStaff(staffId));
    }

    // ─── GET SALARIES BY TYPE ─────────────────────────────────────
    @GetMapping("/type/{salaryType}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<SalaryResponse>> getSalariesByType(
            @PathVariable SalaryType salaryType) {
        return ResponseEntity.ok(salaryService.getSalariesByType(salaryType));
    }
}