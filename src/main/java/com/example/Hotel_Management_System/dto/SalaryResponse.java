package com.example.Hotel_Management_System.dto;

import com.example.Hotel_Management_System.entity.SalaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryResponse {

    private Long salaryId;
    private Long staffId;
    private SalaryType salaryType;
    private String staffName;
    private Double amount ;
    private LocalDateTime effectiveDate;
    private LocalDateTime createdAt;

}
