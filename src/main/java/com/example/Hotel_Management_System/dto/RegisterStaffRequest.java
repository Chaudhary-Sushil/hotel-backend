package com.example.Hotel_Management_System.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterStaffRequest {

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

    @NotNull(message = "dob is required")
    private LocalDate dateOfBirth;

    @Pattern(
            regexp = "ROLE_RECEPTIONIST|ROLE_ADMIN",
            message = "Role must be ROLE_RECEPTIONIST or ROLE_ADMIN"
    )
    private String role;
}
