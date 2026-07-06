package com.example.Hotel_Management_System.exception;

public class RoomAlreadyExistException extends RuntimeException {
    public RoomAlreadyExistException(String message) {
        super(message);
    }
}
