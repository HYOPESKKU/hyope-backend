package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.aicallresult.AiCallResult;
import com.hyope.hyopeserver.entity.*;
import com.hyope.hyopeserver.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

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

    @Transactional
    public void saveAll(AiCallResult dto) {
        if (dto.getRecipientId() == null) {
            throw new IllegalArgumentException("통화 결과 저장 실패: recipientId가 없습니다.");
        }
        if (dto.getCallLog() == null) {
            throw new IllegalArgumentException("통화 결과 저장 실패: callLog 데이터가 없습니다.");
        }
        if (dto.getCallLog().getStartedAt() == null || dto.getCallLog().getEndedAt() == null) {
            throw new IllegalArgumentException("통화 결과 저장 실패: callLog의 시작/종료 시각이 없습니다.");
        }
        if (dto.getAssessment() == null) {
            throw new IllegalArgumentException("통화 결과 저장 실패: assessment 데이터가 없습니다.");
        }
        if (dto.getCallLogEntries() == null) {
            throw new IllegalArgumentException("통화 결과 저장 실패: callLogEntries 데이터가 없습니다.");
        }
        if (dto.getAdherenceRecords() == null) {
            throw new IllegalArgumentException("통화 결과 저장 실패: adherenceRecords 데이터가 없습니다.");
        }

        // 1. Recipient 조회
        Recipient recipient = recipientRepository.findById(dto.getRecipientId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found: " + dto.getRecipientId()));

        // 2. CallLog 저장
        AiCallResult.CallLogData callLogData = dto.getCallLog();
        int durationSec = (int) Duration.between(callLogData.getStartedAt(), callLogData.getEndedAt()).getSeconds();
        CallLog callLog = CallLog.builder()
                .recipient(recipient)
                .startedAt(callLogData.getStartedAt())
                .endedAt(callLogData.getEndedAt())
                .durationSec(durationSec)
                .status(callLogData.getStatus())
                .build();
        CallLog savedCallLog = callLogRepository.save(callLog);

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
                .build();
        Assessment savedAssessment = assessmentRepository.save(assessment);

        // 5. AdherenceRecord 저장
        dto.getAdherenceRecords().forEach(record -> {
            AdherenceRecord adherenceRecord = AdherenceRecord.builder()
                    .assessment(savedAssessment)
                    .adherenceType(record.getAdherenceType())
                    .slot(record.getSlot())
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