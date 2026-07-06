package com.example.Hotel_Management_System.dto;

import com.example.Hotel_Management_System.entity.BookedStatus;
import com.example.Hotel_Management_System.entity.Guest;
import com.example.Hotel_Management_System.entity.Room;
import com.example.Hotel_Management_System.entity.RoomType;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class BookingResponse {

    private Long id;
    private Long guestId;
    private String guestName;        // firstName + lastName
    private String roomNumber;
    private RoomType roomType;
    private Double roomPrice;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bookedDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;

    private BookedStatus bookedStatus;
}