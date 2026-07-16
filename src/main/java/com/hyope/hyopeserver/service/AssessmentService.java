package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.assessment.AssessmentRequest;
import com.hyope.hyopeserver.dto.assessment.AssessmentResponse;
import com.hyope.hyopeserver.entity.Assessment;
import com.hyope.hyopeserver.entity.CallLog;
import com.hyope.hyopeserver.repository.AssessmentRepository;
import com.hyope.hyopeserver.repository.CallLogRepository;
import com.hyope.hyopeserver.repository.RecipientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssessmentService {
    private final RecipientRepository recipientRepository;
    private final CallLogRepository callLogRepository;
    private final AssessmentRepository assessmentRepository;
    private final AdherenceRecordService adherenceRecordService;

    @Transactional
    public AssessmentResponse create(Long callLogId, AssessmentRequest reqDto){
        CallLog callLog = callLogRepository.findById(callLogId)
                .orElseThrow(() -> new IllegalArgumentException("평가 지표 생성 실패: 해당 통화 로그가 없습니다."));

        if (assessmentRepository.existsByCallLogId(callLogId)) {
            throw new IllegalArgumentException("평가 지표 생성 실패: 이미 해당 통화 로그에 대한 지표가 존재합니다.");
        }

        Assessment assessment = reqDto.toEntity(callLog);
        Assessment created = assessmentRepository.save(assessment);
        return AssessmentResponse.from(created);
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