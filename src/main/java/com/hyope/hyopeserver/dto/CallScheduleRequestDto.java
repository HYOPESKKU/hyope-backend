package com.hyope.hyopeserver.dto;

import com.hyope.hyopeserver.entity.CallSchedule;
import com.hyope.hyopeserver.entity.Recipient;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.LocalTime;

@AllArgsConstructor
@ToString
public class CallScheduleRequestDto {
    private Long recipientId;
    private LocalTime scheduledTime;
    private boolean isActive;

    public CallSchedule toEntity(Recipient recipient){
        return new CallSchedule(
                null,
                recipient,
                scheduledTime,
                isActive,
                LocalDateTime.now()
        );
    }
}
