package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.notification.NotificationResponse;
import com.hyope.hyopeserver.entity.CallLog;
import com.hyope.hyopeserver.entity.Guardian;
import com.hyope.hyopeserver.entity.GuardianNotification;
import com.hyope.hyopeserver.entity.Recipient;
import com.hyope.hyopeserver.entity.enums.NotificationType;
import com.hyope.hyopeserver.repository.GuardianRepository;
import com.hyope.hyopeserver.repository.NotificationRepository;
import com.hyope.hyopeserver.repository.RecipientRepository;
import com.hyope.hyopeserver.websocket.NotificationSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final GuardianRepository guardianRepository;
    private final RecipientRepository recipientRepository;
    private final NotificationSocketHandler notificationSocketHandler;

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

        if (!isEnabled(guardian, type)) {
            log.info("알림 생성 건너뜀(설정 꺼짐) - guardianId={}, type={}", guardian.getId(), type);
            return null;
        }

        GuardianNotification notification = GuardianNotification.builder()
                .guardian(guardian)
                .recipient(recipient)
                .type(type)
                .message(message)
                .detail(detail)
                .build();

        GuardianNotification saved = notificationRepository.save(notification);
        notificationSocketHandler.push(guardian.getId(), NotificationResponse.from(saved));
        return saved;
    }

    @Transactional
    public GuardianNotification createCallMissedNotification(CallLog callLog) {
        Recipient recipient = callLog.getRecipient();
        Guardian guardian = recipient.getGuardian();

        if (!isEnabled(guardian, NotificationType.CALL_MISSED)) {
            log.info("알림 생성 건너뜀(설정 꺼짐) - guardianId={}, type=CALL_MISSED", guardian.getId());
            return null;
        }

        GuardianNotification notification = GuardianNotification.builder()
                .guardian(guardian)
                .recipient(recipient)
                .type(NotificationType.CALL_MISSED)
                .message("%s%s님이 전화를 받지 않았습니다.".formatted(
                        recipient.getLastName(), recipient.getFirstName()))
                .build();

        GuardianNotification saved = notificationRepository.save(notification);
        notificationSocketHandler.push(guardian.getId(), NotificationResponse.from(saved));
        return saved;
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

    // 알림 설정 화면의 3개 토글(오늘의 소식/위험도 변동/위급상황)이 어떤 NotificationType을
    // 다스리는지 — 프론트 NotificationSettingsScreen의 _isDailyNews/_isRiskChange 분류와
    // 맞춘다(단, EMERGENCY는 거기선 "위험도 변동" 섹션에 같이 나열되지만, 설정 토글은 별도라
    // 여기선 emergency 전용으로 뗀다).
    private boolean isEnabled(Guardian guardian, NotificationType type) {
        return switch (type) {
            case CALL_MISSED, CALL_FAILED, NEW_REPORT, DAILY_CALL_MISSING -> guardian.isDailyNewsAlertEnabled();
            case HIGH_RISK, SUDDEN_SPIKE, BOTH, INCOMPLETE_ANALYSIS -> guardian.isRiskChangeAlertEnabled();
            case EMERGENCY -> guardian.isEmergencyAlertEnabled();
        };
    }
}