package com.hyope.hyopeserver.scheduler;

import com.hyope.hyopeserver.entity.CallSchedule;
import com.hyope.hyopeserver.repository.CallLogRepository;
import com.hyope.hyopeserver.repository.CallScheduleRepository;
import com.hyope.hyopeserver.service.AiClientService;
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

    @Scheduled(fixedDelay = 60000) // 1분마다 체크
    public void checkAndDispatch() {
        LocalTime now = LocalTime.now();
        LocalTime oneMinuteLater = now.plusMinutes(1);
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        List<CallSchedule> targets = callScheduleRepository
                .findByScheduledTimeBetweenAndIsActiveTrue(now, oneMinuteLater);

        log.info("스케줄 체크: {}~{}, 대상 {}건", now, oneMinuteLater, targets.size());

        for (CallSchedule schedule : targets) {
            Long recipientId = schedule.getRecipient().getId();

            boolean alreadyCalled = callLogRepository
                    .existsByRecipientIdAndStartedAtAfter(recipientId, todayStart);

            if (alreadyCalled) {
                log.info("오늘 이미 통화 완료 - recipientId={}", recipientId);
                continue;
            }

            log.info("AI 서버에 통화 요청 - recipientId={}", recipientId);
            aiClientService.requestCall(schedule.getRecipient());
        }
    }
}
