package com.hyope.hyopeserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class Guardian {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String loginId;

    private String passwordHash;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDateTime createdAt;
}
