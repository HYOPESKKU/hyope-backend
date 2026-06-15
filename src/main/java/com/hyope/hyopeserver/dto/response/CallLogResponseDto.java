package com.hyope.hyopeserver.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hyope.hyopeserver.entity.CallLog;
import com.hyope.hyopeserver.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class CallLogResponseDto {
    private Long id;
    @JsonProperty("recipient_id")
    private Long recipientId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private int durationSec;
    private Status status;

    public static CallLogResponseDto createCallLogResponseDto(CallLog callLog){
        return new CallLogResponseDto(
                callLog.getId(),
                callLog.getRecipient().getId(),
                callLog.getStartedAt(),
                callLog.getEndedAt(),
                callLog.getDurationSec(),
                callLog.getStatus()
        );
    }
}
