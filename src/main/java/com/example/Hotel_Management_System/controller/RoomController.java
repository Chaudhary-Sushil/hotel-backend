package com.example.Hotel_Management_System.controller;

import com.example.Hotel_Management_System.dto.*;
import com.example.Hotel_Management_System.entity.RoomStatus;
import com.example.Hotel_Management_System.entity.RoomType;
import com.example.Hotel_Management_System.repository.BookingRepository;
import com.example.Hotel_Management_System.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final BookingRepository bookingRepository;

    // ─── ADMIN OPERATIONS ─────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RoomResponse> addRoom(
            @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.addRoom(request));
    }

    @PutMapping("/{roomId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable Long roomId,
            @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(roomId, request));
    }

    @DeleteMapping("/{roomId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.deleteRoom(roomId));
    }

    @PatchMapping("/{roomId}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RECEPTIONIST')")
    public ResponseEntity<String> updateRoomStatus(
            @PathVariable Long roomId,
            @RequestParam RoomStatus roomStatus) {
        return ResponseEntity.ok(roomService.updateRoomStatus(roomId, roomStatus));
    }
    // ─── READ OPERATIONS ──────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms() {
        return ResponseEntity.ok(roomService.getAvailableRooms());
    }

    @GetMapping("/number/{roomNumber}")
    public ResponseEntity<RoomResponse> getRoomByNumber(
            @PathVariable String roomNumber) {
        return ResponseEntity.ok(roomService.getRoomsByNumber(roomNumber));
    }

    @GetMapping("/type/{roomType}")
    public ResponseEntity<List<RoomResponse>> getRoomsByType(
            @PathVariable RoomType roomType) {
        return ResponseEntity.ok(roomService.getRoomsByType(roomType));
    }
}