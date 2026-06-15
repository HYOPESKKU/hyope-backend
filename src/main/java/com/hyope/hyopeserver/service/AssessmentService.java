package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.request.AssessmentRequestDto;
import com.hyope.hyopeserver.dto.response.AssessmentResponseDto;
import com.hyope.hyopeserver.entity.Assessment;
import com.hyope.hyopeserver.entity.CallLog;
import com.hyope.hyopeserver.entity.Recipient;
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
    public AssessmentResponseDto create(Long recipientId, Long callLogId, AssessmentRequestDto reqDto){
        Recipient recipient = recipientRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("평가 지표 생성 실패: 해당 돌봄 대상자가 없습니다."));
        CallLog callLog = callLogRepository.findById(callLogId)
                .orElseThrow(() -> new IllegalArgumentException("평가 지표 생성 실패: 해당 통화 로그가 없습니다."));

        if (assessmentRepository.existsByCallLogId(callLogId)) {
            throw new IllegalArgumentException("평가 지표 생성 실패: 이미 해당 통화 로그에 대한 지표가 존재합니다.");
        }

        Assessment assessment = reqDto.toEntity(recipient, callLog);
        Assessment created = assessmentRepository.save(assessment);
        return AssessmentResponseDto.createAssessmentResponseDto(created);
    }

    public AssessmentResponseDto read(Long id){
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("평가 지표 조회 실패: 해당 평가 지표가 없습니다."));
        return AssessmentResponseDto.createAssessmentResponseDto(assessment);
    }

    public List<AssessmentResponseDto> readAll(Long recipientId){
        if (!recipientRepository.existsById(recipientId)) {
            throw new IllegalArgumentException("평가 지표 목록 조회 실패: 해당 돌봄 대상자가 없습니다.");
        }
        return assessmentRepository.findByRecipientId(recipientId)
                .stream()
                .map(AssessmentResponseDto::createAssessmentResponseDto)
                .toList();
    }

    @Transactional
    public AssessmentResponseDto delete(Long id){
        Assessment target = assessmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("평가 지표 삭제 실패: 해당 평가 지표가 없습니다."));
        adherenceRecordService.deleteAllByAssessmentId(id);
        assessmentRepository.delete(target);
        return AssessmentResponseDto.createAssessmentResponseDto(target);
    }

    @Transactional
    public void deleteAllByRecipientId(Long recipientId){
        List<Assessment> assessments = assessmentRepository.findByRecipientId(recipientId);
        for (Assessment assessment : assessments) {
            adherenceRecordService.deleteAllByAssessmentId(assessment.getId());
        }
        assessmentRepository.deleteByRecipientId(recipientId);
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