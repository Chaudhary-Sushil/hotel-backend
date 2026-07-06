package com.example.Hotel_Management_System.service;


import com.example.Hotel_Management_System.dto.RoomRequest;
import com.example.Hotel_Management_System.dto.RoomResponse;
import com.example.Hotel_Management_System.entity.BookedStatus;
import com.example.Hotel_Management_System.entity.Room;
import com.example.Hotel_Management_System.entity.RoomStatus;
import com.example.Hotel_Management_System.entity.RoomType;
import com.example.Hotel_Management_System.exception.RoomAlreadyExistException;
import com.example.Hotel_Management_System.exception.RoomNotFoundException;
import com.example.Hotel_Management_System.repository.BookingRepository;
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

    public RoomResponse addRoom(RoomRequest request) {

        if (roomRepository.findByRoomNumber(request.getRoomNumber()).isPresent()) {
            throw new RoomAlreadyExistException("Room already exists!" + request.getRoomNumber());
        }

        var newRoom = Room.builder().roomNumber(request.getRoomNumber()).roomType(request.getRoomType()).capacity(request.getCapacity()).price(request.getPrice()).build();

        roomRepository.save(newRoom);


        return RoomResponse.builder().roomId(newRoom.getRoomId()).roomNumber(newRoom.getRoomNumber()).roomType(newRoom.getRoomType()).capacity(newRoom.getCapacity()).price(newRoom.getPrice()).roomStatus(newRoom.getRoomStatus()).build();

    }


    public RoomResponse updateRoom(Long roomId, RoomRequest request) {

        var room = roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException("Room not found with id: " + roomId));


        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setCapacity(request.getCapacity());
        room.setPrice(request.getPrice());


        roomRepository.save(room);


        return RoomResponse.builder().roomId(room.getRoomId()).roomNumber(room.getRoomNumber()).roomType(room.getRoomType()).capacity(room.getCapacity()).price(room.getPrice()).roomStatus(room.getRoomStatus()).build();

    }

    public String deleteRoom(Long roomId) {

        var room = roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException("Room not found with id: " + roomId));

        // Block if room is occupied or under maintenance
        if (room.getRoomStatus() == RoomStatus.OCCUPIED || room.getRoomStatus() == RoomStatus.UNDERMAINTENANCE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete room with status: " + room.getRoomStatus());
        }

        // Block only if there are ACTIVE bookings (not past/completed ones)
        boolean hasActiveBookings = bookingRepository.existsByRoomRoomIdAndBookedStatusIn(roomId, List.of(BookedStatus.PENDING, BookedStatus.CONFIRMED));

        if (hasActiveBookings) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete room with active bookings.");
        }

        roomRepository.delete(room);

        return "Room " + room.getRoomNumber() + " deleted successfully ";

    }

    public String updateRoomStatus(Long roomId, RoomStatus roomStatus) {

        var room = roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException("Room not found with id: " + roomId));
        room.setRoomStatus(roomStatus);
        roomRepository.save(room);

        return "Room " + room.getRoomNumber() + " status updated to " + roomStatus + " successfully ";


    }

    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream().map(room -> RoomResponse.builder().roomId(room.getRoomId()).roomNumber(room.getRoomNumber()).roomType(room.getRoomType()).capacity(room.getCapacity()).price(room.getPrice()).roomStatus(room.getRoomStatus()).build()).collect(Collectors.toList());
    }

    public List<RoomResponse> getAvailableRooms() {
        return roomRepository.findByRoomStatus(RoomStatus.AVAILABLE).stream().map(room -> RoomResponse.builder().roomId(room.getRoomId()).roomNumber(room.getRoomNumber()).roomType(room.getRoomType()).capacity(room.getCapacity()).price(room.getPrice()).roomStatus(room.getRoomStatus()).build()).collect(Collectors.toList());
    }

    public RoomResponse getRoomsByNumber(String roomNumber) {

        var room = roomRepository.findByRoomNumber(roomNumber).orElseThrow(() -> new RoomNotFoundException("Room not found: " + roomNumber));

        return RoomResponse.builder().roomId(room.getRoomId()).roomNumber(room.getRoomNumber()).roomType(room.getRoomType()).capacity(room.getCapacity()).price(room.getPrice()).roomStatus(room.getRoomStatus()).build();
    }

    public List<RoomResponse> getRoomsByType(RoomType roomType) {
        return roomRepository.findByRoomType(roomType).stream().map(room -> RoomResponse.builder().roomId(room.getRoomId()).roomNumber(room.getRoomNumber()).roomType(room.getRoomType()).capacity(room.getCapacity()).price(room.getPrice()).roomStatus(room.getRoomStatus()).build()).collect(Collectors.toList());
    }
}
