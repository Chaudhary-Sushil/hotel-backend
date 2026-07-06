package com.example.Hotel_Management_System.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private Path rootLocation;

    @jakarta.annotation.PostConstruct
    public void init() {
        try {
            rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(rootLocation);
            log.info("Upload directory initialized at {}", rootLocation);
        } catch (IOException e) {
            log.error("Could not initialize upload directory: {}", e.getMessage(), e);
            throw new RuntimeException("Could not initialize upload directory", e);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Reject anything that isn't a plain image extension to avoid path traversal / malicious uploads
        if (!extension.matches("(?i)\\.(jpg|jpeg|png|webp)")) {
            throw new IllegalArgumentException("Only jpg, jpeg, png, and webp files are allowed");
        }

        String storedFilename = UUID.randomUUID() + extension;

        try {
            Path destination = rootLocation.resolve(storedFilename).normalize();

            // Defensive check: destination must stay inside rootLocation
            if (!destination.getParent().equals(rootLocation)) {
                throw new SecurityException("Cannot store file outside designated directory");
            }

            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            log.info("Stored file {} as {}", originalFilename, storedFilename);

            // This relative path is what gets saved on RoomImage.imagePath
            return "room-images/" + storedFilename;

        } catch (IOException e) {
            log.error("Failed to store file {}: {}", originalFilename, e.getMessage(), e);
            throw new RuntimeException("Failed to store file " + originalFilename, e);
        }
    }

    public void deleteFile(String relativePath) {
        try {
            String filename = Paths.get(relativePath).getFileName().toString();
            Path target = rootLocation.resolve(filename).normalize();
            Files.deleteIfExists(target);
            log.info("Deleted file {}", filename);
        } catch (IOException e) {
            log.error("Failed to delete file {}: {}", relativePath, e.getMessage(), e);
            // Don't rethrow — a failed file delete shouldn't block the DB delete from succeeding
        }
    }
}
