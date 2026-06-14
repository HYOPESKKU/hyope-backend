package com.hyope.hyopeserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hyope.hyopeserver.entity.AdherenceRecord;
import com.hyope.hyopeserver.entity.AdherenceType;
import com.hyope.hyopeserver.entity.Slot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class AdherenceRecordResponseDto {
    private Long id;

    @JsonProperty("assessment_id")
    private Long assessmentId;

    private AdherenceType adherenceType;
    private Slot slot;

    private boolean isNeeded;
    private boolean isDone;

    public static AdherenceRecordResponseDto createAdherenceRecordResponseDto(AdherenceRecord adherenceRecord){
        return new AdherenceRecordResponseDto(
                adherenceRecord.getId(),
                adherenceRecord.getAssessment().getId(),
                adherenceRecord.getAdherenceType(),
                adherenceRecord.getSlot(),
                adherenceRecord.isNeeded(),
                adherenceRecord.isDone()
        );
    }
}
