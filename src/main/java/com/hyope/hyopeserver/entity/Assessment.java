package com.hyope.hyopeserver.entity;

import com.hyope.hyopeserver.entity.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> evidence;
}