package com.hyope.hyopeserver.dto;

import com.hyope.hyopeserver.entity.Gender;
import com.hyope.hyopeserver.entity.Guardian;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class GuardianRequestDto { //클라이언트에서 받아온 것
    private String loginId;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private Gender gender;

    //toEntity
    public Guardian toEntity(){
        return new Guardian(
                null,
                loginId,
                password,
                firstName,
                lastName,
                phoneNumber,
                email,
                gender,
                LocalDateTime.now()
        );
    }
}
