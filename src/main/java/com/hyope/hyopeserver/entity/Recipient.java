package com.hyope.hyopeserver.entity;

import com.hyope.hyopeserver.dto.recipient.RecipientRequest;
import com.hyope.hyopeserver.entity.enums.Gender;
import com.hyope.hyopeserver.entity.enums.RelationType;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    private RelationType relationToGuardian;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void patch(RecipientRequest reqDto) {
        if(reqDto.getFirstName() != null) this.firstName = reqDto.getFirstName();
        if(reqDto.getLastName() != null) this.lastName = reqDto.getLastName();
        if(reqDto.getBirthDate() != null) this.birthDate = reqDto.getBirthDate();
        if(reqDto.getGender() != null) this.gender = reqDto.getGender();
        if(reqDto.getPhoneNumber() != null) this.phoneNumber = reqDto.getPhoneNumber();
        if(reqDto.getRelationToGuardian() != null) this.relationToGuardian = reqDto.getRelationToGuardian();
    }
}

