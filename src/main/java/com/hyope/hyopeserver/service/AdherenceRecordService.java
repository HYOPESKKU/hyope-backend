package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.adherencerecord.AdherenceRecordRequest;
import com.hyope.hyopeserver.dto.adherencerecord.AdherenceRecordResponse;
import com.hyope.hyopeserver.entity.AdherenceRecord;
import com.hyope.hyopeserver.entity.Assessment;
import com.hyope.hyopeserver.repository.AdherenceRecordRepository;
import com.hyope.hyopeserver.repository.AssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdherenceRecordService {
    private final AssessmentRepository assessmentRepository;
    private final AdherenceRecordRepository adherenceRecordRepository;

    @Transactional
    public AdherenceRecordResponse create(Long assessmentId, AdherenceRecordRequest reqDto){
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("이행 기록 생성 실패: 해당 평가 정보가 없습니다."));
        AdherenceRecord adherenceRecord = reqDto.toEntity(assessment);
        AdherenceRecord created = adherenceRecordRepository.save(adherenceRecord);
        return AdherenceRecordResponse.from(created);
    }

    public AdherenceRecordResponse read(Long id){
        AdherenceRecord adherenceRecord = adherenceRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이행 기록 조회 실패: 해당 이행 기록이 없습니다."));
        return AdherenceRecordResponse.from(adherenceRecord);
    }

    public List<AdherenceRecordResponse> readAll(Long assessmentId){
        if (!assessmentRepository.existsById(assessmentId)) {
            throw new IllegalArgumentException("이행 기록 목록 조회 실패: 해당 평가 정보가 없습니다.");
        }
        return adherenceRecordRepository.findByAssessmentId(assessmentId)
                .stream()
                .map(AdherenceRecordResponse::from)
                .toList();
    }

    @Transactional
    public AdherenceRecordResponse update(Long id, AdherenceRecordRequest reqDto){
        AdherenceRecord target = adherenceRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이행 기록 수정 실패: 해당 이행 기록이 없습니다."));
        target.patch(reqDto);
        AdherenceRecord updated = adherenceRecordRepository.save(target);
        return AdherenceRecordResponse.from(updated);
    }

    @Transactional
    public AdherenceRecordResponse delete(Long id){
        AdherenceRecord target = adherenceRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이행 기록 삭제 실패: 해당 이행 기록이 없습니다."));
        adherenceRecordRepository.delete(target);
        return AdherenceRecordResponse.from(target);
    }

    @Transactional
    public void deleteAllByAssessmentId(Long assessmentId){
        adherenceRecordRepository.deleteByAssessmentId(assessmentId);
    }
}