package com.example.Hotel_Management_System.service;

import com.example.Hotel_Management_System.dto.*;
import com.example.Hotel_Management_System.entity.RoomStatus;
import com.example.Hotel_Management_System.repository.BookingRepository;
import com.example.Hotel_Management_System.repository.PaymentRepository;
import com.example.Hotel_Management_System.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    // ─── SUMMARY ─────────────────────────────────────────────────────────────

    public DashboardSummaryResponse getSummary() {
        long totalRooms    = roomRepository.countByIsDeletedFalse();
        long occupiedRooms = roomRepository.countByRoomStatusAndIsDeletedFalse(RoomStatus.OCCUPIED);
        long availableRooms = roomRepository.countByRoomStatusAndIsDeletedFalse(RoomStatus.AVAILABLE);

        double occupancyRate = totalRooms > 0
                ? Math.round((occupiedRooms * 100.0 / totalRooms) * 10.0) / 10.0
                : 0.0;

        LocalDate today = LocalDate.now();
        long todayCheckIns  = bookingRepository.countTodayCheckIns(today);
        long todayCheckOuts = bookingRepository.countTodayCheckOuts(today);
        long activeBookings = bookingRepository.countActiveBookings();

        return DashboardSummaryResponse.builder()
                .totalRooms(totalRooms)
                .occupiedRooms(occupiedRooms)
                .availableRooms(availableRooms)
                .occupancyRate(occupancyRate)
                .todayCheckIns(todayCheckIns)
                .todayCheckOuts(todayCheckOuts)
                .activeBookings(activeBookings)
                .build();
    }

    // ─── DAILY REVENUE ────────────────────────────────────────────────────────

    /**
     * @param range "7d" or "30d" — any other value defaults to 7 days
     */
    public List<RevenueDailyDto> getDailyRevenue(String range) {
        int days = "30d".equalsIgnoreCase(range) ? 30 : 7;
        LocalDateTime since = LocalDateTime.now().minusDays(days).toLocalDate().atStartOfDay();
        return paymentRepository.getDailyRevenue(since);
    }

    // ─── BOOKING STATUS BREAKDOWN ─────────────────────────────────────────────

    public List<BookingStatusBreakdownDto> getBookingStatusBreakdown() {
        return bookingRepository.countByStatus();
    }

    // ─── RECENT ACTIVITY ─────────────────────────────────────────────────────

    /**
     * Merges latest 5 bookings + latest 5 payments, sorts by timestamp desc,
     * and returns the top 5 overall.
     */
    public List<RecentActivityDto> getRecentActivity() {
        List<RecentActivityDto> activities = new ArrayList<>();

        // Latest 5 bookings
        bookingRepository.findLatestBookings(PageRequest.of(0, 5))
                .forEach(b -> activities.add(RecentActivityDto.builder()
                        .id(b.getId())
                        .type("BOOKING")
                        .guestName(b.getGuest().getFirstName() + " " + b.getGuest().getLastName())
                        .roomNumber(b.getRoom().getRoomNumber())
                        .amount(b.getRoom().getPrice())
                        .status(b.getBookedStatus().name())
                        .timestamp(b.getBookedDate())
                        .build()));

        // Latest 5 payments
        paymentRepository.findLatestPayments(PageRequest.of(0, 5))
                .forEach(p -> activities.add(RecentActivityDto.builder()
                        .id(p.getId())
                        .type("PAYMENT")
                        .guestName(p.getBooking().getGuest().getFirstName() + " " +
                                   p.getBooking().getGuest().getLastName())
                        .roomNumber(p.getBooking().getRoom().getRoomNumber())
                        .amount(p.getAmount())
                        .status(p.getPaymentStatus().name())
                        .timestamp(p.getCreatedAt())
                        .build()));

        // Sort by timestamp desc and take top 5
        activities.sort(Comparator.comparing(RecentActivityDto::getTimestamp,
                Comparator.nullsLast(Comparator.reverseOrder())));

        return activities.size() > 5 ? activities.subList(0, 5) : activities;
    }
}
