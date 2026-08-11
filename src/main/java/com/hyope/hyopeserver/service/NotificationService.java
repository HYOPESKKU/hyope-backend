package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.notification.NotificationResponse;
import com.hyope.hyopeserver.entity.Notification;
import com.hyope.hyopeserver.repository.GuardianRepository;
import com.hyope.hyopeserver.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final GuardianRepository guardianRepository;

    public List<NotificationResponse> readAllByLoginId(String loginId) {
        Long guardianId = guardianRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("알림 조회 실패: 보호자 정보가 없습니다."))
                .getId();

        return notificationRepository.findAllByGuardianId(guardianId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Transactional
    public void markRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("알림 읽음 처리 실패: 해당 알림이 없습니다."));
        notification.markRead();
    }
}