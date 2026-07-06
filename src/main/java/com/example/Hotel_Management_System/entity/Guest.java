package com.example.Hotel_Management_System.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name ="guests")
@DiscriminatorValue("GUEST")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Guest extends User {


    @Column (unique = true)
    private String passportNumber;

    @Column (unique = true)
    private String citizenshipNumber;

    @Column
    private String nationality;

    @Column
    private String address;
}
