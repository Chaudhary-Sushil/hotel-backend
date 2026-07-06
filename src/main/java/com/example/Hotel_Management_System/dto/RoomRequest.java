package com.example.Hotel_Management_System.dto;

import com.example.Hotel_Management_System.entity.RoomType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {
    @NotBlank(message = "Room Number is required")
    private String roomNumber;

    @NotNull(message = "Room Type is required")
    private RoomType roomType;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity can not be less than 1")
    @Max(value = 5, message = "Capacity can not be more than 5")
    private Integer capacity;

    @NotNull(message = "Price is required")
    @Min(0)
    private Double price;

}
