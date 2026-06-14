package com.hyope.hyopeserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
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

    public void patch(Guardian guardian) {
        if (guardian.loginId != null) this.loginId = guardian.loginId;
        if (guardian.passwordHash != null) this.passwordHash = guardian.passwordHash;
        if (guardian.firstName != null) this.firstName = guardian.firstName;
        if (guardian.lastName != null) this.lastName = guardian.lastName;
        if (guardian.phoneNumber != null) this.phoneNumber = guardian.phoneNumber;
        if (guardian.email != null) this.email = guardian.email;
        if (guardian.gender != null) this.gender = guardian.gender;
    }
}
