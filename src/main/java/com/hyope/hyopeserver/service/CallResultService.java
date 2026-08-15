package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.aicallresult.AiCallResult;
import com.hyope.hyopeserver.entity.*;
import com.hyope.hyopeserver.entity.enums.Status;
import com.hyope.hyopeserver.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hyope.hyopeserver.entity.enums.Slot;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CallResultService {

    private final RecipientRepository recipientRepository;
    private final CallLogRepository callLogRepository;
    private final CallLogEntryRepository callLogEntryRepository;
    private final AssessmentRepository assessmentRepository;
    private final AdherenceRecordRepository adherenceRecordRepository;
    private final RecipientProfileService recipientProfileService;
    private final NotificationService notificationService;
    private final AssessmentService assessmentService;

    @Transactional
    public void saveAll(AiCallResult dto) {
        if (dto.getRecipientId() == null) {
            throw new IllegalArgumentException("통화 결과 저장 실패: recipientId가 없습니다.");
        }
        if (dto.getCallLog() == null) {
            throw new IllegalArgumentException("통화 결과 저장 실패: callLog 데이터가 없습니다.");
        }

        boolean isCallCompleted = dto.getCallLog().getStatus() != Status.MISSED
                && dto.getCallLog().getStatus() != Status.FAILED;

        if (isCallCompleted) {
            if (dto.getAssessment() == null) {
                throw new IllegalArgumentException("통화 결과 저장 실패: assessment 데이터가 없습니다.");
            }
            if (dto.getCallLogEntries() == null) {
                throw new IllegalArgumentException("통화 결과 저장 실패: callLogEntries 데이터가 없습니다.");
            }
            if (dto.getAdherenceRecords() == null) {
                throw new IllegalArgumentException("통화 결과 저장 실패: adherenceRecords 데이터가 없습니다.");
            }
        }

        // 1. Recipient 조회
        Recipient recipient = recipientRepository.findById(dto.getRecipientId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found: " + dto.getRecipientId()));

        // 2. CallLog 저장
        // AI 서버가 통화 시작/종료 시각을 못 채운 채 보내는 경우(발신 자체가 실패했거나,
        // 연결은 됐지만 응답을 하나도 못 받고 끊긴 등)가 있다. 예전엔 여기서 바로 예외를 던져
        // 요청 전체가 500으로 실패했고, 그러면 통화 로그도 못 남고 알림도 안 만들어진 채
        // worker.py가 같은 요청을 영원히 재시도했다. 시각이 비어 있으면 저장 시각으로
        // 대체해서라도 통화 기록/알림은 반드시 남긴다.
        AiCallResult.CallLogData callLogData = dto.getCallLog();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startedAt = callLogData.getStartedAt() != null ? callLogData.getStartedAt() : now;
        LocalDateTime endedAt = callLogData.getEndedAt() != null ? callLogData.getEndedAt() : now;
        if (callLogData.getStartedAt() == null || callLogData.getEndedAt() == null) {
            log.warn("callLog의 시작/종료 시각이 없어 저장 시각으로 대체합니다 - recipientId={}", dto.getRecipientId());
        }
        int durationSec = (int) Duration.between(startedAt, endedAt).getSeconds();
        CallLog callLog = CallLog.builder()
                .recipient(recipient)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .durationSec(durationSec)
                .status(callLogData.getStatus())
                .build();
        CallLog savedCallLog = callLogRepository.save(callLog);

        if (savedCallLog.getStatus() == Status.MISSED || savedCallLog.getStatus() == Status.FAILED) {
            notificationService.createCallMissedNotification(savedCallLog);
            log.info("통화 실패/부재중 알림 생성 - recipientId={}, callLogId={}", dto.getRecipientId(), savedCallLog.getId());
            return;
        }

        // 3. CallLogEntry 저장
        dto.getCallLogEntries().forEach(entry -> {
            CallLogEntry callLogEntry = CallLogEntry.builder()
                    .callLog(savedCallLog)
                    .sequence(entry.getSequence())
                    .question(entry.getQuestion())
                    .answer(entry.getAnswer())
                    .askedAt(entry.getAskedAt())
                    .build();
            callLogEntryRepository.save(callLogEntry);
        });

        // 4. Assessment 저장
        AiCallResult.AssessmentData assessmentData = dto.getAssessment();
        Assessment assessment = Assessment.builder()
                .callLog(savedCallLog)
                .measuredAt(assessmentData.getMeasuredAt())
                .depressionScore(assessmentData.getDepressionScore())
                .emotionalStability(assessmentData.getEmotionalStability())
                .healthRisk(assessmentData.getHealthRisk())
                .cognitiveScore(assessmentData.getCognitiveScore())
                .overallRisk(assessmentData.getOverallRisk())
                .riskLevel(assessmentData.getRiskLevel())
                .summary(assessmentData.getSummary())
                .recommendation(assessmentData.getRecommendation())
                .evidence(toEvidenceMap(assessmentData.getEvidence()))
                .incompleteCategories(dto.getIncompleteCategories())
                .build();
        Assessment savedAssessment = assessmentRepository.save(assessment);

        // 리포트 도착/위험도 급등/일부 항목 미수집(연결은 됐지만 유의미한 대화가
        // 오가지 못한 경우 등) 알림 — /call-logs/{id}/assessments 경로(AssessmentService.create)와
        // 동일한 판정 로직을 그대로 재사용해, AI 서버가 보낸 통화 결과에도 똑같이 적용한다.
        assessmentService.checkAndCreateNotification(savedAssessment, recipient.getId());

        // 5. AdherenceRecord 저장
        // 통화가 하루 한 번뿐이라 slot(아침/점심/저녁)을 정확히 구분할 근거가 없다.
        // AI서버가 어떤 slot을 보내든 하루 단위 하나로 통일해서 저장한다.
        dto.getAdherenceRecords().forEach(record -> {
            AdherenceRecord adherenceRecord = AdherenceRecord.builder()
                    .assessment(savedAssessment)
                    .adherenceType(record.getAdherenceType())
                    .slot(Slot.BREAKFAST)
                    .isNeeded(record.isNeeded())
                    .isDone(record.isDone())
                    .build();
            adherenceRecordRepository.save(adherenceRecord);
        });

        // 6. 프로필(취미/근황) 갱신
        recipientProfileService.applyUpdates(dto.getRecipientId(), dto.getProfileUpdates());

        log.info("AI 분석 결과 저장 완료 - recipientId={}, callLogId={}", dto.getRecipientId(), savedCallLog.getId());
    }

    // AI 서버가 보낸 구조화된 Evidence를 DB에 그대로 저장할 수 있는 Map으로 변환.
    // evidence가 없는 경우(placeholder 응답 등)는 null 그대로 반환 — DB 컬럼도 NULL 허용.
    private java.util.Map<String, Object> toEvidenceMap(AiCallResult.Evidence evidence) {
        if (evidence == null) return null;

        java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
        map.put("judgments", evidence.getJudgments());
        map.put("classifier_ratios", evidence.getClassifierRatios());
        map.put("classified_utterances", evidence.getClassifiedUtterances());
        map.put("emergency_count", evidence.getEmergencyCount());
        return map;
    }
}