package com.example.Hotel_Management_System.exception;

public class BookingAlreadyExistException extends RuntimeException {
    public BookingAlreadyExistException(String message) {
        super(message);
    }
}
