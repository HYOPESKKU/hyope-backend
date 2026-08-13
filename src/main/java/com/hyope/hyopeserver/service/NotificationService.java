package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.entity.CallLog;
import com.hyope.hyopeserver.entity.Guardian;
import com.hyope.hyopeserver.entity.GuardianNotification;
import com.hyope.hyopeserver.entity.Recipient;
import com.hyope.hyopeserver.entity.enums.NotificationType;
import com.hyope.hyopeserver.repository.GuardianRepository;
import com.hyope.hyopeserver.repository.NotificationRepository;
import com.hyope.hyopeserver.repository.RecipientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final GuardianRepository guardianRepository;
    private final RecipientRepository recipientRepository;

    public List<GuardianNotification> readAllForGuardian(Long guardianId) {
        return notificationRepository.findByGuardianIdOrderByCreatedAtDesc(guardianId);
    }

    @Transactional
    public GuardianNotification create(Long recipientId, NotificationType type, String message) {
        return create(recipientId, type, message, null);
    }

    @Transactional
    public GuardianNotification create(Long recipientId, NotificationType type, String message, String detail) {
        Recipient recipient = recipientRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("알림 생성 실패: 대상 돌봄대상자가 없습니다."));
        Guardian guardian = recipient.getGuardian();

        GuardianNotification notification = GuardianNotification.builder()
                .guardian(guardian)
                .recipient(recipient)
                .type(type)
                .message(message)
                .detail(detail)
                .build();

        return notificationRepository.save(notification);
    }

    @Transactional
    public GuardianNotification createCallMissedNotification(CallLog callLog) {
        Recipient recipient = callLog.getRecipient();
        Guardian guardian = recipient.getGuardian();

        GuardianNotification notification = GuardianNotification.builder()
                .guardian(guardian)
                .recipient(recipient)
                .type(NotificationType.CALL_MISSED)
                .message("%s%s님이 전화를 받지 않았습니다.".formatted(
                        recipient.getLastName(), recipient.getFirstName()))
                .build();

        return notificationRepository.save(notification);
    }

    @Transactional
    public GuardianNotification markRead(Long id, Long guardianId) {
        GuardianNotification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("알림 읽음 처리 실패: 해당 알림이 없습니다."));

        if (!notification.getGuardian().getId().equals(guardianId)) {
            throw new IllegalArgumentException("알림 읽음 처리 실패: 본인의 알림만 처리할 수 있습니다.");
        }

        notification.markRead();
        return notification;
    }

    @Transactional
    public void delete(Long id, Long guardianId) {
        GuardianNotification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("알림 삭제 실패: 해당 알림이 없습니다."));

        if (!notification.getGuardian().getId().equals(guardianId)) {
            throw new IllegalArgumentException("알림 삭제 실패: 본인의 알림만 삭제할 수 있습니다.");
        }

        notificationRepository.delete(notification);
    }
}