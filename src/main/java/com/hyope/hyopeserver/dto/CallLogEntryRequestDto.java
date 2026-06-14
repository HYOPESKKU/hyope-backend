package com.hyope.hyopeserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hyope.hyopeserver.entity.CallLog;
import com.hyope.hyopeserver.entity.CallLogEntry;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@ToString
public class CallLogEntryRequestDto {
    @JsonProperty("call_log_id")
    private Long callLogId;

    private int sequence;
    private String question;
    private String answer;
    private LocalDateTime askedAt;

    public CallLogEntry toEntity(CallLog callLog){
        return new CallLogEntry(
                null,
                callLog,
                sequence,
                question,
                answer,
                askedAt
        );
    }
}
