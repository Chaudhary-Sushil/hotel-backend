package com.example.Hotel_Management_System.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name="users")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column (nullable = false)
    private String lastName;

    @Column (unique = true , nullable = false)
    private String email;

    @Column (nullable = false)
    private String password;

    @Column (unique = true)
    private String phoneNumber;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;



    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        this.firstLogin = true;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private Boolean isActive;

    @Builder.Default
    @Column(name = "first_login")
    private Boolean firstLogin = true;

    public Boolean getFirstLogin() {
        return this.firstLogin == null || this.firstLogin;
    }
}
