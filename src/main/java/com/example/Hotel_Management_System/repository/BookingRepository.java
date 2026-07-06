package com.example.Hotel_Management_System.repository;

import com.example.Hotel_Management_System.entity.BookedStatus;
import com.example.Hotel_Management_System.entity.Booking;
import com.example.Hotel_Management_System.entity.Guest;
import com.example.Hotel_Management_System.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    // find all bookings for a room
    @Query("SELECT b FROM Booking b WHERE b.room.roomNumber = :roomNumber")
    List<Booking> findByRoomNumber(@Param("roomNumber") String roomNumber);

    List<Booking> findByGuest(Guest guest);
    List<Booking> findByRoom(Room room);

    List<Booking> findByBookedStatus(BookedStatus bookedStatus);

    List<Booking> findByGuestAndBookedStatus(Guest guest, BookedStatus bookedStatus);

    boolean existsByRoomRoomId(Long roomId);

    boolean existsByRoomRoomIdAndBookedStatusIn(Long roomId, List<BookedStatus> statuses);
}
