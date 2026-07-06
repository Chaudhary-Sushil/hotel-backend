package com.example.Hotel_Management_System.service;

import com.example.Hotel_Management_System.dto.ChangePasswordRequest;
import com.example.Hotel_Management_System.dto.ForgotPasswordRequest;
import com.example.Hotel_Management_System.dto.ResetPasswordRequest;
import com.example.Hotel_Management_System.entity.PasswordResetToken;
import com.example.Hotel_Management_System.exception.UserNotFoundException;
import com.example.Hotel_Management_System.repository.PasswordResetTokenRepository;
import com.example.Hotel_Management_System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    // ─── CHANGE PASSWORD ─────────────────────────────────────────
    public String changePassword(
            String email,
            ChangePasswordRequest request) {

        var user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {
            throw new BadCredentialsException(
                    "Current password is incorrect"
            );
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );
        user.setFirstLogin(false);
        userRepository.save(user);

        return "Password changed successfully! ✅";
    }

    // ─── FORGOT PASSWORD ─────────────────────────────────────────
    @Transactional
    public String forgotPassword(ForgotPasswordRequest request) {

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "No account found with this email"
                        ));

        // delete old token if exists
        resetTokenRepository.deleteByEmail(request.getEmail());

        // generate new token
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .email(request.getEmail())
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();

        resetTokenRepository.save(resetToken);

        // prototype → print to console
        System.out.println("=================================");
        System.out.println("PASSWORD RESET TOKEN");
        System.out.println("Email: " + request.getEmail());
        System.out.println("Token: " + token);
        System.out.println("Expires in: 15 minutes");
        System.out.println("=================================");

        return "Reset token generated! Check server console.";
    }

    // ─── RESET PASSWORD ──────────────────────────────────────────
    @Transactional
    public String resetPassword(ResetPasswordRequest request) {

        // find token
        var resetToken = resetTokenRepository
                .findByToken(request.getToken())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Invalid reset token"
                        ));

        // check if used
        if (resetToken.getUsed()) {
            throw new BadCredentialsException(
                    "Token already used"
            );
        }

        // check expiry
        if (resetToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {
            resetTokenRepository.delete(resetToken);
            throw new BadCredentialsException(
                    "Token has expired. Please request a new one."
            );
        }

        // find user and update password
        var user = userRepository
                .findByEmail(resetToken.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );
        userRepository.save(user);

        // mark token as used
        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);

        return "Password reset successfully! ✅";
    }
}