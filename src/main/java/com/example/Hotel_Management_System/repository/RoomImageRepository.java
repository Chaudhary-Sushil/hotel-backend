package com.example.Hotel_Management_System.repository;

import com.example.Hotel_Management_System.entity.RoomImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {
    List<RoomImage> findByRoom_RoomIdOrderByDisplayOrderAsc(Long roomId);
    void deleteByRoom_RoomId(Long roomId);
}
