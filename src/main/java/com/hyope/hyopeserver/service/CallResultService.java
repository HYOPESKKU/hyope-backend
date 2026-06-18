package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.request.AiCallResultDto;
import com.hyope.hyopeserver.entity.*;
import com.hyope.hyopeserver.entity.enums.Status;
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

    @Transactional
    public void saveAll(AiCallResultDto dto) {
        // 1. Recipient 조회
        Recipient recipient = recipientRepository.findById(dto.getRecipientId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found: " + dto.getRecipientId()));

        // 2. CallLog 저장
        AiCallResultDto.CallLogData callLogData = dto.getCallLog();
        int durationSec = (int) Duration.between(callLogData.getStartedAt(), callLogData.getEndedAt()).getSeconds();
        CallLog callLog = new CallLog(
                null,
                recipient,
                callLogData.getStartedAt(),
                callLogData.getEndedAt(),
                durationSec,
                callLogData.getStatus()
        );
        CallLog savedCallLog = callLogRepository.save(callLog);

        // 3. CallLogEntry 저장
        dto.getCallLogEntries().forEach(entry -> {
            CallLogEntry callLogEntry = new CallLogEntry(
                    null,
                    savedCallLog,
                    entry.getSequence(),
                    entry.getQuestion(),
                    entry.getAnswer(),
                    entry.getAskedAt()
            );
            callLogEntryRepository.save(callLogEntry);
        });

        // 4. Assessment 저장
        AiCallResultDto.AssessmentData assessmentData = dto.getAssessment();
        Assessment assessment = new Assessment(
                null,
                recipient,
                savedCallLog,
                assessmentData.getMeasuredAt(),
                assessmentData.getDepressionScore(),
                assessmentData.getEmotionalStability(),
                assessmentData.getHealthRisk(),
                assessmentData.getCognitiveScore(),
                assessmentData.getOverallRisk(),
                assessmentData.getRiskLevel(),
                assessmentData.getSummary(),
                assessmentData.getRecommendation()
        );
        Assessment savedAssessment = assessmentRepository.save(assessment);

        // 5. AdherenceRecord 저장
        dto.getAdherenceRecords().forEach(record -> {
            AdherenceRecord adherenceRecord = new AdherenceRecord(
                    null,
                    savedAssessment,
                    record.getAdherenceType(),
                    record.getSlot(),
                    record.isNeeded(),
                    record.isDone()
            );
            adherenceRecordRepository.save(adherenceRecord);
        });

        log.info("AI 분석 결과 저장 완료 - recipientId={}, callLogId={}", dto.getRecipientId(), savedCallLog.getId());
    }
}