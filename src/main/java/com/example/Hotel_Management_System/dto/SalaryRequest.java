package com.example.Hotel_Management_System.dto;

import com.example.Hotel_Management_System.entity.SalaryType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryRequest {
    @NotNull(message = "Staff is required")
    private Long staffId;

    @NotNull(message = "Amount is required")
    @Min(0)
    private Double amount;

    @NotNull(message = "Salary type is required")
    private SalaryType salaryType;

    @NotNull(message = "Effective date is required")
    private LocalDateTime effectiveDate;
}
