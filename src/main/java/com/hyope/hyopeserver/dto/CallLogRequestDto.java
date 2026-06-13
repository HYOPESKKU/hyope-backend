package com.hyope.hyopeserver.dto;

import com.hyope.hyopeserver.entity.CallLog;
import com.hyope.hyopeserver.entity.Recipient;
import com.hyope.hyopeserver.entity.Status;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@ToString
public class CallLogRequestDto {
    private Long recipientId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private int durationSec;
    private Status status;

    public CallLog toEntity(Recipient recipient){
        return new CallLog(
                null,
                recipient,
                startedAt,
                endedAt,
                durationSec,
                status
        );
    }
}
