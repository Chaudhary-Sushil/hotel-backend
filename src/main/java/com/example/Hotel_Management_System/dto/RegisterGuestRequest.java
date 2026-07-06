package com.example.Hotel_Management_System.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterGuestRequest {
    @NotBlank(message = "FirstName is required")
    private String firstName;

    @NotBlank(message = "LastName is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Size(min= 10, message = "Phone Number should be at least 10 digit")
    private String phoneNumber;

    @NotBlank(message = "Passport number is required")
    private String passportNumber;

    @NotBlank(message = "Citizenship number is required")
    private String citizenshipNumber;

    @NotBlank(message = "Nationality is required")
    private String nationality;

    @NotBlank(message = "Address is required")
    private String address;

}
