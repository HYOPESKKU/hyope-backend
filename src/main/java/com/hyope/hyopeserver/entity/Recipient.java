package com.hyope.hyopeserver.entity;

import com.hyope.hyopeserver.dto.RecipientRequestDto;
import com.hyope.hyopeserver.dto.RecipientResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class Recipient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="guardian_id")
    private Guardian guardian;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phoneNumber;
    private LocalDateTime createdAt;

    public void patch(RecipientRequestDto reqDto, Guardian guardian) {
        if(guardian != null) this.guardian = guardian;
        if(reqDto.getFirstName() != null) this.firstName = reqDto.getFirstName();
        if(reqDto.getLastName() != null) this.lastName = reqDto.getLastName();
        if(reqDto.getBirthDate() != null) this.birthDate = reqDto.getBirthDate();
        if(reqDto.getGender() != null) this.gender = reqDto.getGender();
        if(reqDto.getPhoneNumber() != null) this.phoneNumber = reqDto.getPhoneNumber();
    }
}

