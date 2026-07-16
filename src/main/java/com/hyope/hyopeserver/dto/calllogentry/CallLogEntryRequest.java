package com.hyope.hyopeserver.dto.calllogentry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hyope.hyopeserver.entity.CallLog;
import com.hyope.hyopeserver.entity.CallLogEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CallLogEntryRequest {
    @JsonProperty("call_log_id")
    private Long callLogId;

    private int sequence;
    private String question;
    private String answer;
    private LocalDateTime askedAt;

    public CallLogEntry toEntity(CallLog callLog){
        if(!Objects.equals(callLog.getId(), callLogId)){
            throw new IllegalArgumentException("통화 로그 id가 일치하지 않습니다.");
        }
        return new CallLogEntry(null, callLog, sequence, question, answer, askedAt);
    }
}
