package com.example.Hotel_Management_System.repository;

import com.example.Hotel_Management_System.entity.RefreshToken;
import com.example.Hotel_Management_System.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    @Modifying                                          // ← marks it as write operation
    @Query("DELETE FROM RefreshToken r WHERE r.user = :user")
    void deleteByUser(User user);          // delete old token when user logs in again
}
