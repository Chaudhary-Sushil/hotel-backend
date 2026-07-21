package com.example.Hotel_Management_System.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDto {

    private Long id;
    private String type;          // "BOOKING" or "PAYMENT"
    private String guestName;
    private String roomNumber;
    private Double amount;        // null for pure booking events
    private String status;        // BookedStatus or PaymentStatus as string

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
}
