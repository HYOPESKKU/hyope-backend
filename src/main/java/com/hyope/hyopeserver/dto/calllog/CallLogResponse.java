package com.hyope.hyopeserver.dto.calllog;

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
public class CallLogResponse {
    private Long id;
    @JsonProperty("recipient_id")
    private Long recipientId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private int durationSec;
    private Status status;

    public static CallLogResponse from(CallLog callLog){
        return new CallLogResponse(
                callLog.getId(),
                callLog.getRecipient().getId(),
                callLog.getStartedAt(),
                callLog.getEndedAt(),
                callLog.getDurationSec(),
                callLog.getStatus()
        );
    }
}
