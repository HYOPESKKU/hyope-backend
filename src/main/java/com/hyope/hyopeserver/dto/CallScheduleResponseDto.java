package com.hyope.hyopeserver.dto;

import com.hyope.hyopeserver.entity.CallSchedule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class CallScheduleResponseDto {
    private Long id;
    private Long recipientId;
    private boolean isActive;
    private LocalTime scheduledTime;

    public static CallScheduleResponseDto createCallScheduleResponseDto(CallSchedule callSchedule){
        return new CallScheduleResponseDto(
                callSchedule.getId(),
                callSchedule.getRecipient().getId(),
                callSchedule.isActive(),
                callSchedule.getScheduledTime()
        );
    }
}
