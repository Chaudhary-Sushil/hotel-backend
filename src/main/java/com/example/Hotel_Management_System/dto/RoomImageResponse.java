package com.example.Hotel_Management_System.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomImageResponse {
    private Long id;

    private String imagePath;

    @JsonProperty("isPrimary")
    private boolean isPrimary;

    private Integer displayOrder;

    private Long roomId;
}
