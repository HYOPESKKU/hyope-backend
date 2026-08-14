package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.emergencyalert.EmergencyAlertRequest;
import com.hyope.hyopeserver.dto.emergencyalert.EmergencyAlertResponse;
import com.hyope.hyopeserver.entity.Guardian;
import com.hyope.hyopeserver.entity.Recipient;
import com.hyope.hyopeserver.entity.enums.NotificationType;
import com.hyope.hyopeserver.repository.RecipientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmergencyAlertService {

    private static final String TITLE = "위급 상황 알림";
    // integrations/sms.py의 _emergency_sms_body와 같은 라벨:값 구조 — 문자와 대시보드 알림 포맷을 맞춘다.
    private static final String DETAIL_FORMAT = "대상자: %s님\n감지 내용: %s\n감지 시각: %s";
    private static final DateTimeFormatter DETECTED_AT_FORMAT = DateTimeFormatter.ofPattern("MM월 dd일 HH:mm");

    private final RecipientRepository recipientRepository;
    private final NotificationService notificationService;

    @Transactional
    public EmergencyAlertResponse handle(EmergencyAlertRequest req) {
        if (req.getRecipientId() == null) {
            throw new IllegalArgumentException("위급 알림 처리 실패: recipientId가 없습니다.");
        }

        Recipient recipient = recipientRepository.findById(req.getRecipientId())
                .orElseThrow(() -> new IllegalArgumentException("위급 알림 처리 실패: 해당 돌봄대상자가 없습니다."));
        Guardian guardian = recipient.getGuardian();

        String recipientName = "%s%s".formatted(recipient.getLastName(), recipient.getFirstName());
        String detail = DETAIL_FORMAT.formatted(
                recipientName, req.getSignal(), LocalDateTime.now().format(DETECTED_AT_FORMAT));

        notificationService.create(req.getRecipientId(), NotificationType.EMERGENCY, TITLE, detail);
        log.info("위급 알림 생성: recipientId={}, signal={}, severity={}",
                req.getRecipientId(), req.getSignal(), req.getSeverity());

        return new EmergencyAlertResponse(
                guardian.getPhoneNumber(),
                "%s%s".formatted(guardian.getLastName(), guardian.getFirstName()),
                recipientName
        );
    }
}
