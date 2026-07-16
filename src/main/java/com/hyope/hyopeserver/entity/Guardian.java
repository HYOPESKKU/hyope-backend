package com.hyope.hyopeserver.entity;

import com.hyope.hyopeserver.entity.enums.Gender;
import com.hyope.hyopeserver.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
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
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phoneNumber;
    private String email;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public void patch(Guardian guardian) {
        if (guardian.loginId != null) this.loginId = guardian.loginId;
        if (guardian.passwordHash != null) this.passwordHash = guardian.passwordHash;
        if (guardian.firstName != null) this.firstName = guardian.firstName;
        if (guardian.lastName != null) this.lastName = guardian.lastName;
        if (guardian.phoneNumber != null) this.phoneNumber = guardian.phoneNumber;
        if (guardian.email != null) this.email = guardian.email;
        if (guardian.gender != null) this.gender = guardian.gender;
        if (guardian.birthDate != null) this.birthDate = guardian.birthDate;
    }
}
