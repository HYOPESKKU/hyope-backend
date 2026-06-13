package com.hyope.hyopeserver.dto;

import com.hyope.hyopeserver.entity.Assessment;
import com.hyope.hyopeserver.entity.CallLog;
import com.hyope.hyopeserver.entity.Recipient;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@ToString
public class AssessmentRequestDto {
    private Long recipientId;
    private Long callLogId;
    private LocalDateTime measuredAt;
    private float depressionScore;
    private float emotionalStability;
    private float healthRisk;
    private float cognitiveScore;
    private float overallRisk;

    public Assessment toEntity(Recipient recipient, CallLog callLog){
        return new Assessment(
                null,
                recipient,
                callLog,
                measuredAt,
                depressionScore,
                emotionalStability,
                healthRisk,
                cognitiveScore,
                overallRisk
        );
    }
}
