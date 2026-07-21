package com.example.Hotel_Management_System.repository;

import com.example.Hotel_Management_System.dto.BookingStatusBreakdownDto;
import com.example.Hotel_Management_System.entity.BookedStatus;
import com.example.Hotel_Management_System.entity.Booking;
import com.example.Hotel_Management_System.entity.Guest;
import com.example.Hotel_Management_System.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    // ─── DASHBOARD AGGREGATE QUERIES ────────────────────────────────

    /** Count bookings whose check-in date is today and are active */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.checkInDate = :today " +
           "AND b.bookedStatus IN ('CONFIRMED', 'CHECKED_IN')")
    long countTodayCheckIns(@Param("today") LocalDate today);

    /** Count bookings that were checked out today */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.checkOutDate = :today " +
           "AND b.bookedStatus = 'CHECKED_OUT'")
    long countTodayCheckOuts(@Param("today") LocalDate today);

    /** Count all currently active bookings (CONFIRMED or CHECKED_IN) */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.bookedStatus IN ('CONFIRMED', 'CHECKED_IN')")
    long countActiveBookings();

    /** Booking count grouped by status for pie chart */
    @Query("SELECT new com.example.Hotel_Management_System.dto.BookingStatusBreakdownDto(" +
           "CAST(b.bookedStatus AS string), COUNT(b)) " +
           "FROM Booking b GROUP BY b.bookedStatus")
    List<BookingStatusBreakdownDto> countByStatus();

    /** Latest N bookings ordered by bookedDate desc — for recent activity feed */
    @Query("SELECT b FROM Booking b ORDER BY b.bookedDate DESC")
    List<Booking> findLatestBookings(org.springframework.data.domain.Pageable pageable);
}
