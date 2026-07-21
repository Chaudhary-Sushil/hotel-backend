package com.example.Hotel_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {

    private long totalRooms;
    private long occupiedRooms;
    private long availableRooms;
    private double occupancyRate;     // percentage, e.g. 62.5

    private long todayCheckIns;
    private long todayCheckOuts;
    private long activeBookings;      // CONFIRMED + CHECKED_IN
}
