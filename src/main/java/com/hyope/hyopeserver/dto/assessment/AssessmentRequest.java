package com.hyope.hyopeserver.dto.assessment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hyope.hyopeserver.entity.Assessment;
import com.hyope.hyopeserver.entity.CallLog;
import com.hyope.hyopeserver.entity.Recipient;
import com.hyope.hyopeserver.entity.enums.RiskLevel;
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
public class AssessmentRequest {
    @JsonProperty("recipient_id")
    private Long recipientId;

    @JsonProperty("call_log_id")
    private Long callLogId;

    private LocalDateTime measuredAt;
    private float depressionScore;
    private float emotionalStability;
    private float healthRisk;
    private float cognitiveScore;
    private float overallRisk;
    private RiskLevel riskLevel;
    private String summary;
    private String recommendation;

    public Assessment toEntity(Recipient recipient, CallLog callLog){
        if(!Objects.equals(recipient.getId(), recipientId)){
            throw new IllegalArgumentException("돌봄 대상자 id가 일치하지 않습니다.");
        }
        if(!Objects.equals(callLog.getId(), callLogId)){
            throw new IllegalArgumentException("통화 로그 id가 일치하지 않습니다.");
        }
        return new Assessment(
                null,
                recipient,
                callLog,
                measuredAt,
                depressionScore,
                emotionalStability,
                healthRisk,
                cognitiveScore,
                overallRisk,
                riskLevel,
                summary,
                recommendation
        );
    }
}

