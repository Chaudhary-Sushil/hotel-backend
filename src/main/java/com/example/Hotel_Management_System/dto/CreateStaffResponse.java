package com.example.Hotel_Management_System.dto;

import lombok.*;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStaffResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String employeeId;
    private String role;
    private String temporaryPassword;
    private String message;
    private Set<String> permissions;
}