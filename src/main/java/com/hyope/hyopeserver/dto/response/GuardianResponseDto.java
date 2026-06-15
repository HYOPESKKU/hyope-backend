package com.hyope.hyopeserver.dto.response;

import com.hyope.hyopeserver.entity.enums.Gender;
import com.hyope.hyopeserver.entity.Guardian;
import com.hyope.hyopeserver.entity.enums.RelationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class GuardianResponseDto {
    private Long id;
    private String loginId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private Gender gender;
    private LocalDate birthDate;

    public static GuardianResponseDto createGuardianResponseDto(Guardian guardian){
        return new GuardianResponseDto(
                guardian.getId(),
                guardian.getLoginId(),
                guardian.getFirstName(),
                guardian.getLastName(),
                guardian.getPhoneNumber(),
                guardian.getEmail(),
                guardian.getGender(),
                guardian.getBirthDate()
        );
    }
}
