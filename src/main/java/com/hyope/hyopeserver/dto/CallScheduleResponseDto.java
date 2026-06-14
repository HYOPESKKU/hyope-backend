package com.hyope.hyopeserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("recipient_id")
    private Long recipientId;
    private Boolean isActive;
    private LocalTime scheduledTime;

    public static CallScheduleResponseDto createCallScheduleResponseDto(CallSchedule callSchedule){
        return new CallScheduleResponseDto(
                callSchedule.getId(),
                callSchedule.getRecipient().getId(),
                callSchedule.getIsActive(),
                callSchedule.getScheduledTime()
        );
    }
}
