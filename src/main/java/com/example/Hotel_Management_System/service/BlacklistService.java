package com.example.Hotel_Management_System.service;

import com.example.Hotel_Management_System.entity.BlacklistedToken;
import com.example.Hotel_Management_System.repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtService jwtService;

    // ─── BLACKLIST A TOKEN ────────────────────────────────────────────
    public void blacklistToken(String token) {

        // get expiry from inside the JWT token itself
        Instant expiry = jwtService.extractExpiration(token).toInstant();

        BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                .token(token)
                .expiryDate(expiry)
                .build();

        blacklistedTokenRepository.save(blacklistedToken);
    }

    // ─── CHECK IF TOKEN IS BLACKLISTED ────────────────────────────────
    public boolean isBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }

    // ─── AUTO CLEANUP — runs every day at midnight ────────────────────
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        blacklistedTokenRepository.deleteExpiredTokens(Instant.now());
        System.out.println("Cleaned up expired blacklisted tokens ✅");
    }
}
