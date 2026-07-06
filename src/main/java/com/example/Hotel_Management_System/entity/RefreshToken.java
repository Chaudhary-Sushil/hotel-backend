package com.example.Hotel_Management_System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;                  // the actual refresh token string

    @Column(nullable = false)
    private Instant expiryDate;            // when it expires

    @OneToOne                             // one user → one refresh token
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
