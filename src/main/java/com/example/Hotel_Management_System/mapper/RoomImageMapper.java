package com.example.Hotel_Management_System.mapper;

import com.example.Hotel_Management_System.dto.RoomImageResponse;
import com.example.Hotel_Management_System.entity.RoomImage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoomImageMapper {

    public RoomImageResponse toResponse(RoomImage image) {
        return RoomImageResponse.builder()
                .id(image.getId())
                .imagePath(image.getImagePath())
                .isPrimary(image.isPrimary())
                .displayOrder(image.getDisplayOrder())
                .roomId(image.getRoom().getRoomId())
                .build();
    }

    public List<RoomImageResponse> toResponseList(List<RoomImage> images) {
        return images.stream()
                .map(this::toResponse)
                .toList();
    }
}