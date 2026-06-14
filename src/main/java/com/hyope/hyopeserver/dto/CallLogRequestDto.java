package com.hyope.hyopeserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hyope.hyopeserver.entity.CallLog;
import com.hyope.hyopeserver.entity.Recipient;
import com.hyope.hyopeserver.entity.Status;
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
public class CallLogRequestDto {
    @JsonProperty("recipient_id")
    private Long recipientId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private int durationSec;
    private Status status;

    public CallLog toEntity(Recipient recipient){
        int calculatedDuration = (int) java.time.Duration.between(startedAt, endedAt).getSeconds();
        return new CallLog(
                null,
                recipient,
                startedAt,
                endedAt,
                calculatedDuration,
                status
        );
    }
}
