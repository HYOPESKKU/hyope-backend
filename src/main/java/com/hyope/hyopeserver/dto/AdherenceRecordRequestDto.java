package com.hyope.hyopeserver.dto;

import com.hyope.hyopeserver.entity.AdherenceRecord;
import com.hyope.hyopeserver.entity.AdherenceType;
import com.hyope.hyopeserver.entity.Assessment;
import com.hyope.hyopeserver.entity.Slot;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class AdherenceRecordRequestDto {
    private Long assessmentId;
    private AdherenceType adherenceType;
    private Slot slot;
    private boolean isNeeded;
    private boolean isDone;

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
