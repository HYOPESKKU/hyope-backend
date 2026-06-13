package com.hyope.hyopeserver.dto;

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
public class CallLogEntryResponseDto {
    private Long callLogId;
    private int sequence;
    private String question;
    private String answer;
    private LocalDateTime askedAt;

    public static CallLogEntryResponseDto createCallLogEntryResponseDto (CallLogEntry callLogEntry){
        return new CallLogEntryResponseDto(
            callLogEntry.getCallLog().getId(),
            callLogEntry.getSequence(),
            callLogEntry.getQuestion(),
            callLogEntry.getAnswer(),
            callLogEntry.getAskedAt()
        );
    }
}
