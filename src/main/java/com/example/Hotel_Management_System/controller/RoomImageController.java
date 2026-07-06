package com.example.Hotel_Management_System.controller;

import com.example.Hotel_Management_System.dto.RoomImageResponse;
import com.example.Hotel_Management_System.entity.Room;
import com.example.Hotel_Management_System.entity.RoomImage;
import com.example.Hotel_Management_System.exception.ResourceNotFoundException;
import com.example.Hotel_Management_System.mapper.RoomImageMapper;
import com.example.Hotel_Management_System.repository.RoomImageRepository;
import com.example.Hotel_Management_System.repository.RoomRepository;
import com.example.Hotel_Management_System.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/rooms/{roomId}/images")
@RequiredArgsConstructor
public class RoomImageController {

    private final RoomImageRepository roomImageRepository;
    private final RoomRepository roomRepository;
    private final FileStorageService fileStorageService;
    private final RoomImageMapper roomImageMapper;

    @GetMapping
    public ResponseEntity<List<RoomImageResponse>> getImagesForRoom(@PathVariable Long roomId) {
        List<RoomImage> images = roomImageRepository.findByRoom_RoomIdOrderByDisplayOrderAsc(roomId);
        return ResponseEntity.ok(roomImageMapper.toResponseList(images));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomImageResponse> uploadImage(
            @PathVariable Long roomId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPrimary", defaultValue = "false") boolean isPrimary,
            @RequestParam(value = "displayOrder", required = false) Integer displayOrder) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));

        String storedPath = fileStorageService.storeFile(file);

        RoomImage image = RoomImage.builder()
                .imagePath(storedPath)
                .isPrimary(isPrimary)
                .displayOrder(displayOrder)
                .room(room)
                .build();

        RoomImage saved = roomImageRepository.save(image);
        log.info("Uploaded image {} for room {}", saved.getImagePath(), roomId);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomImageMapper.toResponse(saved));
    }

    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteImage(@PathVariable Long roomId, @PathVariable Long imageId) {
        RoomImage image = roomImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));

        fileStorageService.deleteFile(image.getImagePath());
        roomImageRepository.delete(image);

        log.info("Deleted image {} from room {}", imageId, roomId);
        return ResponseEntity.noContent().build();
    }
}