package com.hyope.hyopeserver.dto.guardian;

import com.hyope.hyopeserver.entity.enums.Gender;
import com.hyope.hyopeserver.entity.Guardian;

import java.time.LocalDate;

import com.hyope.hyopeserver.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class SignupRequest {
    private String loginId;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String phoneNumber;
    private String email;
    private Gender gender;
    private Role role;

    public Guardian toEntity(PasswordEncoder encoder){
        return Guardian.builder()
                .loginId(loginId)
                .passwordHash(password != null ? encoder.encode(password) : null)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .email(email)
                .gender(gender)
                .birthDate(birthDate)
                .role(role)
                .build();
    }
}
