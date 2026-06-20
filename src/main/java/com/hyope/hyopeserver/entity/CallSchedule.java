package com.hyope.hyopeserver.entity;

import com.hyope.hyopeserver.dto.request.CallScheduleRequestDto;
import com.hyope.hyopeserver.entity.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter

public class CallSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="recipient_id")
    private Recipient recipient;

    private LocalTime scheduledTime;
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private Status lastCallStatus;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void patch(CallScheduleRequestDto reqDto, Recipient recipient) {
        if (recipient != null) this.recipient = recipient;
        if (reqDto.getScheduledTime() != null) this.scheduledTime = reqDto.getScheduledTime();
        if (reqDto.getIsActive() != null) this.isActive = reqDto.getIsActive();
    }

    public void updateLastCallStatus(Status status) {
        this.lastCallStatus = status;
    }
}
