package com.hyope.hyopeserver.dto.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hyope.hyopeserver.entity.Notification;
import com.hyope.hyopeserver.entity.enums.NotificationReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NotificationResponse {
    private Long id;

    @JsonProperty("recipient_id")
    private Long recipientId;

    private String type;
    private String message;

    @JsonProperty("isRead")
    private boolean isRead;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        Long recipientId = notification.getAssessment()
                .getCallLog()
                .getRecipient()
                .getId();

        return new NotificationResponse(
                notification.getId(),
                recipientId,
                notification.getReason().name(),
                buildMessage(notification.getReason(), notification.getAssessment().getOverallRisk()),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }

    private static String buildMessage(NotificationReason reason, float overallRisk) {
        return switch (reason) {
            case HIGH_RISK -> "위험도가 기준치를 초과했습니다. (%.1f점)".formatted(overallRisk);
            case SUDDEN_SPIKE -> "위험도가 전일 대비 급격히 상승했습니다. (%.1f점)".formatted(overallRisk);
            case BOTH -> "위험도가 높고, 전일 대비 급격히 상승했습니다. (%.1f점)".formatted(overallRisk);
        };
    }
}