package com.example.Hotel_Management_System.service;

import com.example.Hotel_Management_System.dto.BookingRequest;
import com.example.Hotel_Management_System.dto.BookingResponse;
import com.example.Hotel_Management_System.entity.BookedStatus;
import com.example.Hotel_Management_System.entity.Booking;
import com.example.Hotel_Management_System.entity.RoomStatus;
import com.example.Hotel_Management_System.exception.BookingNotFoundException;
import com.example.Hotel_Management_System.exception.GuestNotFoundException;
import com.example.Hotel_Management_System.exception.RoomNotAvailableException;
import com.example.Hotel_Management_System.exception.RoomNotFoundException;
import com.example.Hotel_Management_System.repository.BookingRepository;
import com.example.Hotel_Management_System.repository.GuestRepository;
import com.example.Hotel_Management_System.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;

    public BookingResponse createBooking(BookingRequest request, String email) {

        // 1. get guest from token email
        var guest = guestRepository.findByEmail(email)
                .orElseThrow(() -> new GuestNotFoundException(
                        "Guest not found: " + email
                ));

        // 2. find room by id
        var room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException(
                        "Room not found: " + request.getRoomId()
                ));

        // 3. check room is AVAILABLE
        if (room.getRoomStatus() != RoomStatus.AVAILABLE) {
            throw new RoomNotAvailableException(
                    "Room " + room.getRoomNumber() + " is not available"
            );
        }

        // 4. create booking
        var booking = Booking.builder()
                .guest(guest)
                .room(room)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .bookedStatus(BookedStatus.PENDING)
                .build();

        bookingRepository.save(booking);

        // 5. set room status to OCCUPIED
        room.setRoomStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);

        // 6. return BookingResponse
        return BookingResponse.builder()
                .id(booking.getId())
                .guestId(guest.getId())
                .guestName(guest.getFirstName() + " " + guest.getLastName())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .roomPrice(room.getPrice())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .bookedStatus(booking.getBookedStatus())
                .bookedDate(booking.getBookedDate())
                .build();
    }

    public BookingResponse cancelBooking(Long bookingId, String email) {

        // 1. find booking
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking not found: " + bookingId
                ));

        // 2. check if this booking belongs to this guest
        if (!booking.getGuest().getEmail().equals(email)) {
            throw new AccessDeniedException(
                    "You can only cancel your own bookings"
            );
        }

        // 3. check booking can be cancelled
        if (booking.getBookedStatus() == BookedStatus.CHECKED_IN ||
                booking.getBookedStatus() == BookedStatus.CHECKED_OUT ||
                booking.getBookedStatus() == BookedStatus.CANCELLED) {
            throw new RoomNotAvailableException(
                    "Booking cannot be cancelled at this stage"
            );
        }

        // 4. cancel booking
        booking.setBookedStatus(BookedStatus.CANCELLED);
        bookingRepository.save(booking);

        // 5. set room back to AVAILABLE
        var room = booking.getRoom();
        room.setRoomStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);

        // 6. return response
        return BookingResponse.builder()
                .id(booking.getId())
                .guestId(booking.getGuest().getId())
                .guestName(booking.getGuest().getFirstName() + " " +
                        booking.getGuest().getLastName())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .roomPrice(room.getPrice())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .bookedStatus(booking.getBookedStatus())
                .bookedDate(booking.getBookedDate())
                .build();
    }

    // ─── GET MY BOOKINGS ─────────────────────────────────────────────
    public List<BookingResponse> getMyBookings(String email) {

        var guest = guestRepository.findByEmail(email)
                .orElseThrow(() -> new GuestNotFoundException(
                        "Guest not found: " + email
                ));

        return bookingRepository.findByGuest(guest)
                .stream()
                .map(booking -> BookingResponse.builder()
                        .id(booking.getId())
                        .guestId(booking.getGuest().getId())
                        .guestName(booking.getGuest().getFirstName() + " " +
                                booking.getGuest().getLastName())
                        .roomNumber(booking.getRoom().getRoomNumber())
                        .roomType(booking.getRoom().getRoomType())
                        .roomPrice(booking.getRoom().getPrice())
                        .checkInDate(booking.getCheckInDate())
                        .checkOutDate(booking.getCheckOutDate())
                        .bookedStatus(booking.getBookedStatus())
                        .bookedDate(booking.getBookedDate())
                        .build())
                .collect(Collectors.toList());
    }

    // ─── CONFIRM BOOKING ─────────────────────────────────────────────
    public BookingResponse confirmBooking(Long bookingId) {

        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking not found: " + bookingId
                ));

        if (booking.getBookedStatus() != BookedStatus.PENDING) {
            throw new RoomNotAvailableException(
                    "Only PENDING bookings can be confirmed"
            );
        }

        booking.setBookedStatus(BookedStatus.CONFIRMED);
        bookingRepository.save(booking);

        return buildBookingResponse(booking);
    }

    // ─── CHECK IN ────────────────────────────────────────────────────
    public BookingResponse checkIn(Long bookingId) {

        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking not found: " + bookingId
                ));

        if (booking.getBookedStatus() != BookedStatus.CONFIRMED) {
            throw new RoomNotAvailableException(
                    "Only CONFIRMED bookings can be checked in"
            );
        }

        booking.setBookedStatus(BookedStatus.CHECKED_IN);
        bookingRepository.save(booking);

        return buildBookingResponse(booking);
    }

    // ─── CHECK OUT ───────────────────────────────────────────────────
    public BookingResponse checkOut(Long bookingId) {

        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking not found: " + bookingId
                ));

        if (booking.getBookedStatus() != BookedStatus.CHECKED_IN) {
            throw new RoomNotAvailableException(
                    "Only CHECKED_IN bookings can be checked out"
            );
        }

        // set booking status
        booking.setBookedStatus(BookedStatus.CHECKED_OUT);
        bookingRepository.save(booking);

        // set room back to AVAILABLE
        var room = booking.getRoom();
        room.setRoomStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);

        return buildBookingResponse(booking);
    }

    // ─── GET ALL BOOKINGS ─────────────────────────────────────────────
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::buildBookingResponse)
                .collect(Collectors.toList());
    }

    // ─── GET BOOKING BY ID ────────────────────────────────────────────
    public BookingResponse getBookingById(Long bookingId) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking not found: " + bookingId
                ));
        return buildBookingResponse(booking);
    }

    // ─── HELPER METHOD ───────────────────────────────────────────────
    private BookingResponse buildBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .guestId(booking.getGuest().getId())
                .guestName(booking.getGuest().getFirstName() + " " +
                        booking.getGuest().getLastName())
                .roomNumber(booking.getRoom().getRoomNumber())
                .roomType(booking.getRoom().getRoomType())
                .roomPrice(booking.getRoom().getPrice())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .bookedStatus(booking.getBookedStatus())
                .bookedDate(booking.getBookedDate())
                .build();
    }
}
