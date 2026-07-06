package com.example.Hotel_Management_System.exception;

public class StaffAlreadyExistsException extends RuntimeException {
    public StaffAlreadyExistsException(String message) {
        super(message);
    }
}
