package com.hyope.hyopeserver.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hyope.hyopeserver.entity.AdherenceRecord;
import com.hyope.hyopeserver.entity.enums.AdherenceType;
import com.hyope.hyopeserver.entity.Assessment;
import com.hyope.hyopeserver.entity.enums.Slot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdherenceRecordRequestDto {
    @JsonProperty("assessment_id")
    private Long assessmentId;

    private AdherenceType adherenceType;
    private Slot slot;
    private Boolean isNeeded;
    private Boolean isDone;

    public AdherenceRecord toEntity(Assessment assessment){
        return new AdherenceRecord(
                null,
                assessment,
                adherenceType,
                slot,
                isNeeded,
                isDone
        );
    }
}
