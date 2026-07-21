package com.example.Hotel_Management_System.controller;

import com.example.Hotel_Management_System.dto.*;
import com.example.Hotel_Management_System.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Analytics Dashboard Controller
 *
 * Design decision: these are read-only aggregate endpoints kept separate from
 * the existing feature controllers (BookingController, RoomController, etc.)
 * to avoid bloating them. All data is computed via JPA GROUP BY queries —
 * no in-memory aggregation loops.
 *
 * Access: restricted to ADMIN and RECEPTIONIST roles only.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /api/dashboard/summary
     * Returns: total rooms, occupied rooms, occupancy rate %, today's check-ins,
     * today's check-outs, and active booking count.
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RECEPTIONIST')")
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    /**
     * GET /api/dashboard/revenue?range=7d|30d
     * Returns: list of {date, revenue} pairs for the last 7 or 30 days.
     * Only counts PAID payments. Missing days (no revenue) are omitted —
     * the frontend fills the gaps for a smooth line chart.
     */
    @GetMapping("/revenue")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RECEPTIONIST')")
    public ResponseEntity<List<RevenueDailyDto>> getRevenue(
            @RequestParam(defaultValue = "7d") String range) {
        return ResponseEntity.ok(dashboardService.getDailyRevenue(range));
    }

    /**
     * GET /api/dashboard/booking-status-breakdown
     * Returns: list of {status, count} pairs for every BookedStatus that
     * has at least one booking (PENDING, CONFIRMED, CHECKED_IN, etc.)
     */
    @GetMapping("/booking-status-breakdown")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RECEPTIONIST')")
    public ResponseEntity<List<BookingStatusBreakdownDto>> getBookingStatusBreakdown() {
        return ResponseEntity.ok(dashboardService.getBookingStatusBreakdown());
    }

    /**
     * GET /api/dashboard/recent-activity
     * Returns: top 5 most recent events (bookings + payments merged and sorted by time).
     */
    @GetMapping("/recent-activity")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RECEPTIONIST')")
    public ResponseEntity<List<RecentActivityDto>> getRecentActivity() {
        return ResponseEntity.ok(dashboardService.getRecentActivity());
    }
}
