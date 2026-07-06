package com.example.Hotel_Management_System.entity;

public enum PaymentMethod {
    CASH,
    CARD,
    ONLINE;

    public static void main(String[] args) {
        System.out.println(CASH.name());
    }// should print CASH
}
