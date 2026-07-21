package com.example.Hotel_Management_System.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueDailyDto {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private Double revenue;
}
