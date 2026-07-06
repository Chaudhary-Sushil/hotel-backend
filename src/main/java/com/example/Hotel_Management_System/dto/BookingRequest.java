package com.example.Hotel_Management_System.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotNull(message = "Room is required")
    private Long roomId;                    // ← only roomId needed

    @NotNull(message = "Check in date is required")

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @NotNull(message = "Check out date is required")
    @Future(message = "Check- out date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;
}
