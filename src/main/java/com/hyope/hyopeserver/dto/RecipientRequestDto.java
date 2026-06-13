package com.hyope.hyopeserver.dto;

import com.hyope.hyopeserver.entity.Gender;
import com.hyope.hyopeserver.entity.Guardian;
import com.hyope.hyopeserver.entity.Recipient;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@ToString
public class RecipientRequestDto {
    private Long guardianId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String phoneNumber;

    //toEntity
    public Recipient toEntity(Guardian guardian){
        return new Recipient(
                null,
                guardian,
                firstName,
                lastName,
                birthDate,
                gender,
                phoneNumber,
                LocalDateTime.now()
        );
    }
}
