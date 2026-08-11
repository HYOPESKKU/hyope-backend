package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.entity.CallLog;
import com.hyope.hyopeserver.entity.Guardian;
import com.hyope.hyopeserver.entity.GuardianNotification;
import com.hyope.hyopeserver.entity.Recipient;
import com.hyope.hyopeserver.entity.enums.NotificationType;
import com.hyope.hyopeserver.entity.enums.Status;
import com.hyope.hyopeserver.repository.GuardianNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final GuardianNotificationRepository guardianNotificationRepository;

    @Transactional
    public void createCallMissedNotification(CallLog callLog) {
        Recipient recipient = callLog.getRecipient();
        Guardian guardian = recipient.getGuardian();
        if (guardian == null) {
            log.warn("통화 미연결 알림 생성 skip - recipientId={}에 연결된 보호자가 없습니다.", recipient.getId());
            return;
        }

        boolean missed = callLog.getStatus() == Status.MISSED;
        NotificationType type = missed ? NotificationType.CALL_MISSED : NotificationType.CALL_FAILED;
        String name = recipient.getLastName() + recipient.getFirstName();
        String message = missed
                ? name + "님과의 통화가 연결되지 않았습니다."
                : name + "님과의 통화 연결에 실패했습니다.";

        GuardianNotification notification = GuardianNotification.builder()
                .guardian(guardian)
                .recipient(recipient)
                .type(type)
                .message(message)
                .build();
        guardianNotificationRepository.save(notification);
    }

    public List<GuardianNotification> readAllForGuardian(Long guardianId) {
        return guardianNotificationRepository.findByGuardianIdOrderByCreatedAtDesc(guardianId);
    }

    @Transactional
    public GuardianNotification markRead(Long notificationId, Long guardianId) {
        GuardianNotification notification = guardianNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림 읽음 처리 실패: 해당 알림이 없습니다."));
        if (!notification.getGuardian().getId().equals(guardianId)) {
            throw new IllegalArgumentException("알림 읽음 처리 실패: 본인 알림이 아닙니다.");
        }
        notification.markRead();
        return notification;
    }
}
