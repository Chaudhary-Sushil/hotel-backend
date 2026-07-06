package com.example.Hotel_Management_System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name ="bookings")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "guest_id", nullable = false)
    private Guest  guest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "room_id", nullable = false)
    private Room room;


    @Column(updatable = false, nullable = false)
    private LocalDateTime bookedDate;

    @PrePersist
    protected void onCreate(){
        this.bookedDate = LocalDateTime.now();
    }

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column( nullable = false)
    private LocalDate checkOutDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    @Builder.Default
    private BookedStatus bookedStatus = BookedStatus.PENDING;

}
