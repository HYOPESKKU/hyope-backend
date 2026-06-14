package com.hyope.hyopeserver.dto;

import com.hyope.hyopeserver.entity.Gender;
import com.hyope.hyopeserver.entity.Guardian;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class GuardianRequestDto {
    private String loginId;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private Gender gender;

    public Guardian toEntity(PasswordEncoder encoder){
        return Guardian.builder()
                .loginId(loginId)
                .passwordHash(password != null ? encoder.encode(password) : null)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .email(email)
                .gender(gender)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
