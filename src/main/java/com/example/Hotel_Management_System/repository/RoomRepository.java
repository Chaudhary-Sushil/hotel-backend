package com.example.Hotel_Management_System.repository;

import com.example.Hotel_Management_System.entity.Room;
import com.example.Hotel_Management_System.entity.RoomStatus;
import com.example.Hotel_Management_System.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomNumber(String roomNumber);
    boolean existsByRoomNumber(String roomNumber);
    List<Room> findByRoomStatus(RoomStatus roomStatus);
    List<Room> findByRoomType(RoomType roomType);
    List<Room> findByRoomStatusAndRoomType(RoomStatus roomStatus, RoomType roomType);
    List<Room> findAllByIsDeletedFalse();
    List<Room> findByRoomStatusAndIsDeletedFalse(RoomStatus roomStatus);
    List<Room> findByRoomTypeAndIsDeletedFalse(RoomType roomType);

    // ─── DASHBOARD COUNT HELPERS ─────────────────────────────────────

    /** Total non-deleted rooms */
    long countByIsDeletedFalse();

    /** Non-deleted rooms by specific status */
    long countByRoomStatusAndIsDeletedFalse(RoomStatus status);
}