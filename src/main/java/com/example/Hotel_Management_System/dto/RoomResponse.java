package com.example.Hotel_Management_System.dto;

import com.example.Hotel_Management_System.entity.RoomStatus;
import com.example.Hotel_Management_System.entity.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long roomId;
    private String roomNumber;
    private RoomType roomType;
    private Integer capacity;
    private Double price;
    private RoomStatus roomStatus;
}
