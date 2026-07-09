package com.example.Hotel_Management_System.service;


import com.example.Hotel_Management_System.dto.RoomRequest;
import com.example.Hotel_Management_System.dto.RoomResponse;
import com.example.Hotel_Management_System.entity.*;
import com.example.Hotel_Management_System.exception.RoomAlreadyExistException;
import com.example.Hotel_Management_System.exception.RoomNotFoundException;
import com.example.Hotel_Management_System.repository.BookingRepository;
import com.example.Hotel_Management_System.repository.RoomImageRepository;
import com.example.Hotel_Management_System.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final RoomImageRepository roomImageRepository;

    public RoomResponse addRoom(RoomRequest request) {

        var existing = roomRepository.findByRoomNumber(request.getRoomNumber());

        if (existing.isPresent()) {
            var room = existing.get();

            if (!room.isDeleted()) {
                throw new RoomAlreadyExistException("Room already exists!" + request.getRoomNumber());
            }

            // Reactivate the archived room instead of creating a duplicate
            room.setDeleted(false);
            room.setRoomType(request.getRoomType());
            room.setCapacity(request.getCapacity());
            room.setPrice(request.getPrice());
            room.setRoomStatus(RoomStatus.AVAILABLE);
            roomRepository.save(room);

            return toResponse(room);
        }

        var newRoom = Room.builder()
                .roomNumber(request.getRoomNumber())
                .roomType(request.getRoomType())
                .capacity(request.getCapacity())
                .price(request.getPrice())
                .build();

        roomRepository.save(newRoom);

        return toResponse(newRoom);
    }


    public RoomResponse updateRoom(Long roomId, RoomRequest request) {

        var room = roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException("Room not found with id: " + roomId));

        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setCapacity(request.getCapacity());
        room.setPrice(request.getPrice());

        roomRepository.save(room);

        return toResponse(room);
    }

    public String deleteRoom(Long roomId) {

        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with id: " + roomId));

        // Block if room is occupied or under maintenance
        if (room.getRoomStatus() == RoomStatus.OCCUPIED || room.getRoomStatus() == RoomStatus.UNDERMAINTENANCE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete room with status: " + room.getRoomStatus());
        }

        // Block only if there are ACTIVE bookings (not past/completed ones)
        boolean hasActiveBookings = bookingRepository.existsByRoomRoomIdAndBookedStatusIn(
                roomId, List.of(BookedStatus.PENDING, BookedStatus.CONFIRMED));

        if (hasActiveBookings) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete room with active bookings.");
        }

        // Room has no active bookings, but may still have past/completed booking history
        boolean hasAnyBookingHistory = bookingRepository.existsByRoomRoomId(roomId);

        if (hasAnyBookingHistory) {
            // Soft delete: preserve booking/payment history, just hide the room going forward
            room.setDeleted(true);
            roomRepository.save(room);
            return "Room " + room.getRoomNumber() + " archived (booking history preserved).";
        }

        // Never booked at all — safe to fully remove
        roomRepository.delete(room);
        return "Room " + room.getRoomNumber() + " deleted successfully.";
    }

    public String updateRoomStatus(Long roomId, RoomStatus roomStatus) {

        var room = roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException("Room not found with id: " + roomId));
        room.setRoomStatus(roomStatus);
        roomRepository.save(room);

        return "Room " + room.getRoomNumber() + " status updated to " + roomStatus + " successfully ";
    }

    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAllByIsDeletedFalse().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<RoomResponse> getAvailableRooms() {
        return roomRepository.findByRoomStatusAndIsDeletedFalse(RoomStatus.AVAILABLE).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RoomResponse getRoomsByNumber(String roomNumber) {

        var room = roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new RoomNotFoundException("Room not found: " + roomNumber));

        return toResponse(room);
    }

    public List<RoomResponse> getRoomsByType(RoomType roomType) {
        return roomRepository.findByRoomTypeAndIsDeletedFalse(roomType).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private RoomResponse toResponse(Room room) {

        String primaryImageUrl = roomImageRepository
                .findFirstByRoom_RoomIdAndIsPrimaryTrue(room.getRoomId())
                .or(() -> roomImageRepository.findFirstByRoom_RoomIdOrderByDisplayOrderAsc(room.getRoomId()))
                .map(RoomImage::getImagePath)
                .orElse(null);

        return RoomResponse.builder()
                .roomId(room.getRoomId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .capacity(room.getCapacity())
                .price(room.getPrice())
                .roomStatus(room.getRoomStatus())
                .primaryImageUrl(primaryImageUrl)
                .build();
    }


}