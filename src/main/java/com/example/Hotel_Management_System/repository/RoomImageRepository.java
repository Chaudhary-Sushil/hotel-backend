package com.example.Hotel_Management_System.repository;

import com.example.Hotel_Management_System.entity.RoomImage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {
    List<RoomImage> findByRoom_RoomIdOrderByDisplayOrderAsc(Long roomId);
    void deleteByRoom_RoomId(Long roomId);

    // New: for enforcing single-primary and computing primaryImageUrl
    List<RoomImage> findByRoom_RoomIdAndIsPrimaryTrue(Long roomId);
    Optional<RoomImage> findFirstByRoom_RoomIdAndIsPrimaryTrue(Long roomId);
    Optional<RoomImage> findFirstByRoom_RoomIdOrderByDisplayOrderAsc(Long roomId);
}