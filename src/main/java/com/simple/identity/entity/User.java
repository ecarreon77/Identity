package com.simple.identity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String role;

    @Column(length = 500)
    private String authToken;

    @Column(nullable = false)
    private Boolean isActivated = false;

    @Column(nullable = false)
    private String contactNumber;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private String sex;

}
