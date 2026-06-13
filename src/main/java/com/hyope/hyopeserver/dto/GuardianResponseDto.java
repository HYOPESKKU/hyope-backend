package com.hyope.hyopeserver.dto;

import com.hyope.hyopeserver.entity.Gender;
import com.hyope.hyopeserver.entity.Guardian;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    public static GuardianResponseDto createGuardianResponseDto(Guardian guardian){
        return new GuardianResponseDto(
                guardian.getId(),
                guardian.getLoginId(),
                guardian.getFirstName(),
                guardian.getLastName(),
                guardian.getPhoneNumber(),
                guardian.getEmail(),
                guardian.getGender()
        );
    }
}
