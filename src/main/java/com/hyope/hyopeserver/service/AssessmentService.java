package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.assessment.AssessmentRequest;
import com.hyope.hyopeserver.dto.assessment.AssessmentResponse;
import com.hyope.hyopeserver.entity.Assessment;
import com.hyope.hyopeserver.entity.CallLog;
import com.hyope.hyopeserver.entity.enums.NotificationType;
import com.hyope.hyopeserver.entity.enums.RiskLevel;
import com.hyope.hyopeserver.repository.AssessmentRepository;
import com.hyope.hyopeserver.repository.CallLogRepository;
import com.hyope.hyopeserver.repository.RecipientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssessmentService {
    private static final double SUDDEN_SPIKE_THRESHOLD = 1.5;

    private final RecipientRepository recipientRepository;
    private final CallLogRepository callLogRepository;
    private final AssessmentRepository assessmentRepository;
    private final AdherenceRecordService adherenceRecordService;
    private final NotificationService notificationService;

    @Transactional
    public AssessmentResponse create(Long callLogId, AssessmentRequest reqDto){
        CallLog callLog = callLogRepository.findById(callLogId)
                .orElseThrow(() -> new IllegalArgumentException("평가 지표 생성 실패: 해당 통화 로그가 없습니다."));

        if (assessmentRepository.existsByCallLogId(callLogId)) {
            throw new IllegalArgumentException("평가 지표 생성 실패: 이미 해당 통화 로그에 대한 지표가 존재합니다.");
        }

        Assessment assessment = reqDto.toEntity(callLog);
        Assessment created = assessmentRepository.save(assessment);

        checkAndCreateNotification(created, callLog.getRecipient().getId());

        return AssessmentResponse.from(created);
    }

    private void checkAndCreateNotification(Assessment current, Long recipientId) {
        boolean isHighRisk = current.getRiskLevel() == RiskLevel.WARNING
                || current.getRiskLevel() == RiskLevel.HIGH;

        List<Assessment> previousList = assessmentRepository
                .findByCallLog_Recipient_IdAndMeasuredAtBeforeOrderByMeasuredAtDesc(
                        recipientId, current.getMeasuredAt());

        boolean isSuddenSpike = false;
        if (!previousList.isEmpty()) {
            double previousRisk = previousList.get(0).getOverallRisk();
            double diff = current.getOverallRisk() - previousRisk;
            isSuddenSpike = diff >= SUDDEN_SPIKE_THRESHOLD;
        }

        if (isHighRisk || isSuddenSpike) {
            NotificationType type;
            String message;
            if (isHighRisk && isSuddenSpike) {
                type = NotificationType.BOTH;
                message = "위험도가 높고, 전일 대비 급격히 상승했습니다. (%.1f점)".formatted(current.getOverallRisk());
            } else if (isHighRisk) {
                type = NotificationType.HIGH_RISK;
                message = "위험도가 기준치를 초과했습니다. (%.1f점)".formatted(current.getOverallRisk());
            } else {
                type = NotificationType.SUDDEN_SPIKE;
                message = "위험도가 전일 대비 급격히 상승했습니다. (%.1f점)".formatted(current.getOverallRisk());
            }

            notificationService.create(recipientId, type, message);
            log.info("알림 생성: recipientId={}, assessmentId={}, type={}",
                    recipientId, current.getId(), type);
        }

        List<String> incompleteCategories = current.getIncompleteCategories();
        if (incompleteCategories != null && !incompleteCategories.isEmpty()) {
            String categoryList = String.join(", ", incompleteCategories);
            String message = "일부 항목(%s)의 분석 데이터가 수집되지 않았습니다.".formatted(categoryList);
            notificationService.create(recipientId, NotificationType.INCOMPLETE_ANALYSIS, message);
            log.info("알림 생성: recipientId={}, assessmentId={}, type=INCOMPLETE_ANALYSIS, categories={}",
                    recipientId, current.getId(), categoryList);
        }
    }

    public AssessmentResponse read(Long id){
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("평가 지표 조회 실패: 해당 평가 지표가 없습니다."));
        return AssessmentResponse.from(assessment);
    }

    public List<AssessmentResponse> readAll(Long recipientId){
        if (!recipientRepository.existsById(recipientId)) {
            throw new IllegalArgumentException("평가 지표 목록 조회 실패: 해당 돌봄 대상자가 없습니다.");
        }
        return assessmentRepository.findByCallLog_Recipient_Id(recipientId)
                .stream()
                .map(AssessmentResponse::from)
                .toList();
    }

    @Transactional
    public AssessmentResponse delete(Long id){
        Assessment target = assessmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("평가 지표 삭제 실패: 해당 평가 지표가 없습니다."));
        adherenceRecordService.deleteAllByAssessmentId(id);
        assessmentRepository.delete(target);
        return AssessmentResponse.from(target);
    }

    @Transactional
    public void deleteAllByRecipientId(Long recipientId){
        List<Assessment> assessments = assessmentRepository.findByCallLog_Recipient_Id(recipientId);
        for (Assessment assessment : assessments) {
            adherenceRecordService.deleteAllByAssessmentId(assessment.getId());
        }
        assessmentRepository.deleteByCallLog_Recipient_Id(recipientId);
    }

    @Transactional
    public void deleteByCallLogId(Long callLogId){
        Assessment assessment = assessmentRepository.findByCallLogId(callLogId).orElse(null);
        if (assessment != null) {
            adherenceRecordService.deleteAllByAssessmentId(assessment.getId());
        }
        assessmentRepository.deleteByCallLogId(callLogId);
    }
}