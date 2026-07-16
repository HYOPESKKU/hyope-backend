package com.hyope.hyopeserver.dto.calllogentry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hyope.hyopeserver.entity.CallLogEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class CallLogEntryResponse {
    @JsonProperty("call_log_id")
    private Long callLogId;

    private int sequence;
    private String question;
    private String answer;
    private LocalDateTime askedAt;

    public static CallLogEntryResponse from(CallLogEntry callLogEntry){
        return new CallLogEntryResponse(
            callLogEntry.getCallLog().getId(),
            callLogEntry.getSequence(),
            callLogEntry.getQuestion(),
            callLogEntry.getAnswer(),
            callLogEntry.getAskedAt()
        );
    }
}
