package com.hyope.hyopeserver.dto.notification;

import com.hyope.hyopeserver.entity.GuardianNotification;
import com.hyope.hyopeserver.entity.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class NotificationResponse {

    private Long id;
    private Long recipientId;
    private NotificationType type;
    private String message;
    private String detail;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponse from(GuardianNotification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getRecipient().getId(),
                notification.getType(),
                notification.getMessage(),
                notification.getDetail(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}