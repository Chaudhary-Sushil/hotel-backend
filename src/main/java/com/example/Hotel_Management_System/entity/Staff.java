package com.example.Hotel_Management_System.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name="staff")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@DiscriminatorValue("STAFF")
public class Staff extends User {

    @Column(unique = true, nullable = false)
    private String employeeId;

    @Column( nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinDate;

    @Override
    protected void onCreate() {
        super.onCreate();
        this.joinDate = LocalDateTime.now();

    }

    private LocalDateTime retiredDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "staff_permissions", joinColumns = @JoinColumn(name = "staff_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permission")
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

}
