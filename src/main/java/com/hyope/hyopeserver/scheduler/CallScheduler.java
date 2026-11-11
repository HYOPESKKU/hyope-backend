package com.hyope.hyopeserver.scheduler;

import com.hyope.hyopeserver.entity.CallSchedule;
import com.hyope.hyopeserver.entity.enums.NotificationType;
import com.hyope.hyopeserver.repository.CallLogRepository;
import com.hyope.hyopeserver.repository.CallScheduleRepository;
import com.hyope.hyopeserver.service.AiClientService;
import com.hyope.hyopeserver.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CallScheduler {

    private final CallScheduleRepository callScheduleRepository;
    private final CallLogRepository callLogRepository;
    private final AiClientService aiClientService;
    private final NotificationService notificationService;

    @Scheduled(fixedDelay = 60000) // 1분마다 체크
    public void checkAndDispatch() {
        LocalTime now = LocalTime.now();
        LocalTime oneMinuteLater = now.plusMinutes(1);

        List<CallSchedule> targets = callScheduleRepository
                .findByScheduledTimeBetweenAndIsActiveTrue(now, oneMinuteLater);

        log.info("스케줄 체크: {}~{}, 대상 {}건", now, oneMinuteLater, targets.size());

        for (CallSchedule schedule : targets) {
            Long recipientId = schedule.getRecipient().getId();

            int intervalDays = schedule.getIntervalDays() != null ? schedule.getIntervalDays() : 1;
            LocalDateTime cutoff = LocalDate.now().minusDays(intervalDays - 1).atStartOfDay();
            if (callLogRepository.existsByRecipientIdAndStartedAtAfter(recipientId, cutoff)) {
                log.info("간격 미충족으로 스킵 - recipientId={}, intervalDays={}", recipientId, intervalDays);
                continue;
            }

            log.info("AI 서버에 통화 요청 - recipientId={}", recipientId);
            aiClientService.requestCall(schedule);
        }
    }

    // 매일 23:55에, 오늘 예정된 스케줄이었는데 실제 통화 기록이 없는 대상자에게 알림을 보낸다.
    @Scheduled(cron = "0 59 23 * * *")
    public void checkDailyCallMissing() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        List<CallSchedule> activeSchedules = callScheduleRepository.findByIsActiveTrue();

        log.info("일일 통화 누락 체크: 활성 스케줄 {}건", activeSchedules.size());

        for (CallSchedule schedule : activeSchedules) {
            Long recipientId = schedule.getRecipient().getId();
            String recipientName = schedule.getRecipient().getLastName() + schedule.getRecipient().getFirstName();

            boolean hasCallToday = callLogRepository
                    .existsByRecipientIdAndStartedAtAfter(recipientId, todayStart);

            if (!hasCallToday) {
                notificationService.create(
                        recipientId,
                        NotificationType.DAILY_CALL_MISSING,
                        "오늘 %s님과 예정된 통화가 진행되지 않았습니다.".formatted(recipientName)
                );
                log.info("일일 통화 누락 알림 생성 - recipientId={}", recipientId);
            }
        }
    }
}