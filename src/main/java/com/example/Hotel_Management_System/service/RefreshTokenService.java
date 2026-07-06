package com.example.Hotel_Management_System.service;

import com.example.Hotel_Management_System.entity.RefreshToken;
import com.example.Hotel_Management_System.entity.User;
import com.example.Hotel_Management_System.exception.RefreshTokenExpiredException;
import com.example.Hotel_Management_System.repository.RefreshTokenRepository;
import com.example.Hotel_Management_System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpiration;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    // ─── CREATE REFRESH TOKEN ─────────────────────────────────────────
    @Transactional
    public RefreshToken createRefreshToken(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // delete old refresh token if exists — one token per user
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())   // random unique string
                .expiryDate(Instant.now().plusMillis(refreshExpiration))
                .user(user)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    // ─── VERIFY REFRESH TOKEN ─────────────────────────────────────────
    public RefreshToken verifyExpiration(RefreshToken token) {

        if (token.getExpiryDate().isBefore(Instant.now())) {
            // token expired → delete it and throw exception
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException(
                    "Refresh token expired. Please login again."
            );
        }
        return token;
    }

    // ─── FIND BY TOKEN STRING ─────────────────────────────────────────
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenExpiredException(
                        "Refresh token not found. Please login again."
                ));
    }



    // ─── DELETE TOKEN ON LOGOUT ───────────────────────────────────────
    @Transactional
    public void deleteByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        refreshTokenRepository.deleteByUser(user);
    }
}
