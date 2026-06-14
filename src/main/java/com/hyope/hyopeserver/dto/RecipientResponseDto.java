package com.hyope.hyopeserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hyope.hyopeserver.entity.Gender;
import com.hyope.hyopeserver.entity.Recipient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class RecipientResponseDto {
    private Long id;

    @JsonProperty("guardian_id")
    private Long guardianId;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String phoneNumber;

    public static RecipientResponseDto createRecipientResponseDto(Recipient recipient){
        return new RecipientResponseDto(
                recipient.getId(),
                recipient.getGuardian().getId(),
                recipient.getFirstName(),
                recipient.getLastName(),
                recipient.getBirthDate(),
                recipient.getGender(),
                recipient.getPhoneNumber()
        );

    }
}
