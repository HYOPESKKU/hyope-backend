package com.hyope.hyopeserver.entity;

import com.hyope.hyopeserver.entity.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter

public class CallLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="recipient_id")
    private Recipient recipient;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private int durationSec;

    @Enumerated(EnumType.STRING)
    private Status status;
}

