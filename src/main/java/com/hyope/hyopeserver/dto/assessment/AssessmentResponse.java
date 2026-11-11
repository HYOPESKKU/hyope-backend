package com.hyope.hyopeserver.dto.assessment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hyope.hyopeserver.entity.Assessment;
import com.hyope.hyopeserver.entity.enums.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class AssessmentResponse {

    private Long id;

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

    public static AssessmentResponse from(Assessment assessment){
        return new AssessmentResponse(
                assessment.getId(),
                assessment.getCallLog().getId(),
                assessment.getMeasuredAt(),
                assessment.getDepressionScore(),
                assessment.getEmotionalStability(),
                assessment.getHealthRisk(),
                assessment.getCognitiveScore(),
                assessment.getOverallRisk(),
                assessment.getRiskLevel(),
                assessment.getSummary(),
                assessment.getRecommendation()
        );
    }
}
