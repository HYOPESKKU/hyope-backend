package com.hyope.hyopeserver.dto.aicallresult;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hyope.hyopeserver.entity.enums.RiskLevel;
import com.hyope.hyopeserver.entity.enums.Status;
import com.hyope.hyopeserver.entity.enums.AdherenceType;
import com.hyope.hyopeserver.entity.enums.Slot;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class AiCallResult {

    @JsonProperty("recipient_id")
    private Long recipientId;

    @JsonProperty("call_log")
    private CallLogData callLog;

    @JsonProperty("call_log_entries")
    private List<CallLogEntryData> callLogEntries;

    private AssessmentData assessment;

    @JsonProperty("adherence_records")
    private List<AdherenceRecordData> adherenceRecords;

    @JsonProperty("profile_updates")
    private List<ProfileUpdateData> profileUpdates;

    @Getter
    @NoArgsConstructor
    public static class CallLogData {
        @JsonProperty("started_at")
        private LocalDateTime startedAt;
        @JsonProperty("ended_at")
        private LocalDateTime endedAt;
        private Status status;
    }

    @Getter
    @NoArgsConstructor
    public static class CallLogEntryData {
        private int sequence;
        private String question;
        private String answer;
        @JsonProperty("asked_at")
        private LocalDateTime askedAt;
    }

    @Getter
    @NoArgsConstructor
    public static class AssessmentData {
        @JsonProperty("measured_at")
        private LocalDateTime measuredAt;
        @JsonProperty("depression_score")
        private float depressionScore;
        @JsonProperty("emotional_stability")
        private float emotionalStability;
        @JsonProperty("health_risk")
        private float healthRisk;
        @JsonProperty("cognitive_score")
        private float cognitiveScore;
        @JsonProperty("overall_risk")
        private float overallRisk;
        @JsonProperty("risk_level")
        private RiskLevel riskLevel;
        private String summary;
        private String recommendation;
    }

    @Getter
    @NoArgsConstructor
    public static class AdherenceRecordData {
        @JsonProperty("adherence_type")
        private AdherenceType adherenceType;
        private Slot slot;
        @JsonProperty("is_needed")
        private boolean isNeeded;
        @JsonProperty("is_done")
        private boolean isDone;
    }

    @Getter
    @NoArgsConstructor
    public static class ProfileUpdateData {
        private String action;   // "add" | "remove"
        private String kind;     // "hobby" | "event"
        private String text;
        @JsonProperty("event_id")
        private String eventId;
        private String reason;
    }
}