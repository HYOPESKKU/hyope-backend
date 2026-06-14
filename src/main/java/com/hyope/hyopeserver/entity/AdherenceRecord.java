package com.hyope.hyopeserver.entity;

import com.hyope.hyopeserver.dto.AdherenceRecordRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter

public class AdherenceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @Enumerated(EnumType.STRING)
    private AdherenceType adherenceType;

    @Enumerated(EnumType.STRING)
    private Slot slot;

    private boolean isNeeded;
    private boolean isDone;

    // AdherenceRecord 엔티티
    public void patch(AdherenceRecordRequestDto reqDto) {
        if (reqDto.getAdherenceType() != null) this.adherenceType = reqDto.getAdherenceType();
        if (reqDto.getSlot() != null) this.slot = reqDto.getSlot();
        if (reqDto.getIsNeeded() != null) this.isNeeded = reqDto.getIsNeeded();
        if (reqDto.getIsDone() != null) this.isDone = reqDto.getIsDone();
    }
}

