package com.hyope.hyopeserver.dto.guardian;

import com.hyope.hyopeserver.entity.enums.Gender;
import com.hyope.hyopeserver.entity.Guardian;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GuardianPatchRequest {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String phoneNumber;
    private String email;
    private Gender gender;
    private Boolean dailyNewsAlertEnabled;
    private Boolean riskChangeAlertEnabled;
    private Boolean emergencyAlertEnabled;
    // loginId, password, role 없음 → 이 경로로는 절대 못 바꿈

    public Guardian toEntity() {
        return Guardian.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthDate(birthDate)
                .phoneNumber(phoneNumber)
                .email(email)
                .gender(gender)
                .dailyNewsAlertEnabled(dailyNewsAlertEnabled)
                .riskChangeAlertEnabled(riskChangeAlertEnabled)
                .emergencyAlertEnabled(emergencyAlertEnabled)
                .build();
    }
}