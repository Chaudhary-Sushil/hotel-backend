package com.example.Hotel_Management_System.repository;

import com.example.Hotel_Management_System.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    boolean existsByToken(String token);    // check if token is blacklisted

    @Modifying
    @Query("DELETE FROM BlacklistedToken b WHERE b.expiryDate < :now")
    void deleteExpiredTokens(Instant now);  // cleanup expired tokens from DB
}
