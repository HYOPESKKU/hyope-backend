package com.hyope.hyopeserver.dto.recipient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hyope.hyopeserver.entity.enums.Gender;
import com.hyope.hyopeserver.entity.Guardian;
import com.hyope.hyopeserver.entity.Recipient;
import com.hyope.hyopeserver.entity.enums.RelationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RecipientRequest {
    @JsonProperty("guardian_id")
    private Long guardianId;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String phoneNumber;
    private RelationType relationToGuardian;

    public Recipient toEntity(Guardian guardian){
        if(!Objects.equals(guardian.getId(), guardianId)){
            throw new IllegalArgumentException("보호자 id가 일치하지 않습니다.");
        }
        return new Recipient(
                null,
                guardian,
                firstName,
                lastName,
                birthDate,
                gender,
                phoneNumber,
                relationToGuardian,
                LocalDateTime.now()
        );
    }
}