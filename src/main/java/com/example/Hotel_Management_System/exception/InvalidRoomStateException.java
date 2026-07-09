package com.example.Hotel_Management_System.exception;

public class InvalidRoomStateException extends RuntimeException {
    public InvalidRoomStateException(String message) {
        super(message);
    }
}