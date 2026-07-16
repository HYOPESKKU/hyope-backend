package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.recipient.RecipientRequest;
import com.hyope.hyopeserver.dto.recipient.RecipientResponse;
import com.hyope.hyopeserver.entity.Guardian;
import com.hyope.hyopeserver.entity.Recipient;
import com.hyope.hyopeserver.repository.GuardianRepository;
import com.hyope.hyopeserver.repository.RecipientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecipientService {
    private final GuardianRepository guardianRepository;
    private final RecipientRepository recipientRepository;
    private final CallLogService callLogService;
    private final CallScheduleService callScheduleService;
    private final AssessmentService assessmentService;

    @Transactional
    public RecipientResponse create(Long guardianId, RecipientRequest reqDto){
        Guardian guardian = guardianRepository.findById(guardianId)
                .orElseThrow(() -> new IllegalArgumentException("돌봄 대상자 정보 생성 실패: 대상 보호자가 없습니다."));
        Recipient recipient = reqDto.toEntity(guardian);
        Recipient created = recipientRepository.save(recipient);
        return RecipientResponse.from(created);
    }

    public RecipientResponse read(Long id){
        Recipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("돌봄 대상자 조회 실패: 해당 돌봄 대상자가 없습니다."));
        return RecipientResponse.from(recipient);
    }

    public List<RecipientResponse> readAll(Long guardianId){
        if (!guardianRepository.existsById(guardianId)) {
            throw new IllegalArgumentException("돌봄 대상자 목록 조회 실패: 대상 보호자가 없습니다.");
        }
        return recipientRepository.findByGuardianId(guardianId)
                .stream()
                .map(RecipientResponse::from)
                .toList();
    }

    @Transactional
    public RecipientResponse update(Long id, RecipientRequest reqDto){
        Recipient target = recipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("돌봄 대상자 정보 수정 실패: 해당 돌봄 대상자가 없습니다."));
        target.patch(reqDto); // 보호자 변경은 이 엔드포인트에서 허용하지 않음 (항상 null 전달)
        Recipient updated = recipientRepository.save(target);
        return RecipientResponse.from(updated);
    }

    @Transactional
    public RecipientResponse delete(Long id){
        Recipient target = recipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("돌봄 대상자 정보 삭제 실패: 해당 돌봄 대상자가 없습니다."));
        recipientRepository.delete(target);
        return RecipientResponse.from(target);
    }

    @Transactional
    public void deleteAllByGuardianId(Long guardianId){
        List<Recipient> recipients = recipientRepository.findByGuardianId(guardianId);
        for (Recipient recipient : recipients) {
            callLogService.deleteAllByRecipientId(recipient.getId());
            callScheduleService.deleteAllByRecipientId(recipient.getId());
            assessmentService.deleteAllByRecipientId(recipient.getId());
        }
        recipientRepository.deleteByGuardianId(guardianId);
    }
}