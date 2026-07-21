package com.example.Hotel_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatusBreakdownDto {

    private String status;   // e.g. "CONFIRMED"
    private Long count;
}
