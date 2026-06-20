package com.hyope.hyopeserver.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hyope.hyopeserver.entity.CallSchedule;
import com.hyope.hyopeserver.entity.Recipient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CallScheduleRequestDto {
    @JsonProperty("recipient_id")
    private Long recipientId;

    private LocalTime scheduledTime;
    private Boolean isActive;

    public CallSchedule toEntity(Recipient recipient){
        if(!Objects.equals(recipient.getId(), recipientId)){
            throw new IllegalArgumentException("돌봄 대상자 id가 일치하지 않습니다.");
        }
        return new CallSchedule(
                null,
                recipient,
                scheduledTime,
                isActive != null ? isActive : true,
                null,
                LocalDateTime.now()
        );
    }
}