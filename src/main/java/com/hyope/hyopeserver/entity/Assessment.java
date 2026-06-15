package com.hyope.hyopeserver.entity;

import com.hyope.hyopeserver.entity.enums.RiskLevel;
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

public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="recipient_id")
    private Recipient recipient;

    @OneToOne
    @JoinColumn(name="call_log_id", unique = true)
    private CallLog callLog;

    private LocalDateTime measuredAt;
    private float depressionScore;
    private float emotionalStability;
    private float healthRisk;
    private float cognitiveScore;
    private float overallRisk;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    private String summary;
    private String recommendation;
}