package com.hyope.hyopeserver.dto.adherencerecord;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hyope.hyopeserver.entity.AdherenceRecord;
import com.hyope.hyopeserver.entity.enums.AdherenceType;
import com.hyope.hyopeserver.entity.enums.Slot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class AdherenceRecordResponse {
    private Long id;

    @JsonProperty("assessment_id")
    private Long assessmentId;

    private AdherenceType adherenceType;
    private Slot slot;

    private boolean isNeeded;
    private boolean isDone;

    public static AdherenceRecordResponse from(AdherenceRecord adherenceRecord){
        return new AdherenceRecordResponse(
                adherenceRecord.getId(),
                adherenceRecord.getAssessment().getId(),
                adherenceRecord.getAdherenceType(),
                adherenceRecord.getSlot(),
                adherenceRecord.isNeeded(),
                adherenceRecord.isDone()
        );
    }
}
