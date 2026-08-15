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

    // 알림 설정 3종(오늘의 소식/위험도 변동/위급상황) — 셋 다 null 허용.
    // null = "한 번도 설정 안 건드림"이고 이 경우 켜진 것으로 취급한다(NotificationService의
    // isXEnabled() 참고). 이렇게 해야 기존 가입자(DB엔 컬럼이 새로 생겨 NULL로 들어옴)와
    // 신규 가입자 모두 마이그레이션 없이 "기본 켜짐"이 유지된다.
    private Boolean dailyNewsAlertEnabled;
    private Boolean riskChangeAlertEnabled;
    private Boolean emergencyAlertEnabled;

    public boolean isDailyNewsAlertEnabled() {
        return dailyNewsAlertEnabled == null || dailyNewsAlertEnabled;
    }

    public boolean isRiskChangeAlertEnabled() {
        return riskChangeAlertEnabled == null || riskChangeAlertEnabled;
    }

    public boolean isEmergencyAlertEnabled() {
        return emergencyAlertEnabled == null || emergencyAlertEnabled;
    }

    public void patch(Guardian guardian) {
        if (guardian.loginId != null) this.loginId = guardian.loginId;
        if (guardian.passwordHash != null) this.passwordHash = guardian.passwordHash;
        if (guardian.firstName != null) this.firstName = guardian.firstName;
        if (guardian.lastName != null) this.lastName = guardian.lastName;
        if (guardian.phoneNumber != null) this.phoneNumber = guardian.phoneNumber;
        if (guardian.email != null) this.email = guardian.email;
        if (guardian.gender != null) this.gender = guardian.gender;
        if (guardian.birthDate != null) this.birthDate = guardian.birthDate;
        if (guardian.dailyNewsAlertEnabled != null) this.dailyNewsAlertEnabled = guardian.dailyNewsAlertEnabled;
        if (guardian.riskChangeAlertEnabled != null) this.riskChangeAlertEnabled = guardian.riskChangeAlertEnabled;
        if (guardian.emergencyAlertEnabled != null) this.emergencyAlertEnabled = guardian.emergencyAlertEnabled;
    }
}
