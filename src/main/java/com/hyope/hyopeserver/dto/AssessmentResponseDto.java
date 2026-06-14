package com.hyope.hyopeserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hyope.hyopeserver.entity.Assessment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class AssessmentResponseDto {
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

    public static AssessmentResponseDto createAssessmentResponseDto(Assessment assessment){
        return new AssessmentResponseDto(
                assessment.getRecipient().getId(),
                assessment.getCallLog().getId(),
                assessment.getMeasuredAt(),
                assessment.getDepressionScore(),
                assessment.getEmotionalStability(),
                assessment.getHealthRisk(),
                assessment.getCognitiveScore(),
                assessment.getOverallRisk()
        );
    }
}
