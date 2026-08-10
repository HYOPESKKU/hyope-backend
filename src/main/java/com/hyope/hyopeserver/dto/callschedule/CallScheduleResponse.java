package com.hyope.hyopeserver.dto.callschedule;

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
public class CallScheduleResponse {
    private Long id;
    @JsonProperty("recipient_id")
    private Long recipientId;
    private Boolean isActive;
    private LocalTime scheduledTime;
    private Integer intervalDays;

    public static CallScheduleResponse from(CallSchedule callSchedule){
        return new CallScheduleResponse(
                callSchedule.getId(),
                callSchedule.getRecipient().getId(),
                callSchedule.getIsActive(),
                callSchedule.getScheduledTime(),
                callSchedule.getIntervalDays()
        );
    }
}
