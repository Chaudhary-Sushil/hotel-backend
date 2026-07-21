package com.example.Hotel_Management_System.dto;

import com.example.Hotel_Management_System.entity.PaymentMethod;
import com.example.Hotel_Management_System.entity.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {
    private Long paymentId;
    private Long guestId;
    private String guestName;
    private Long bookingId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime paidDate;
    private String qrToken;
}
