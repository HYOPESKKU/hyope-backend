package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.request.RecipientRequestDto;
import com.hyope.hyopeserver.dto.response.RecipientResponseDto;
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
    public RecipientResponseDto create(Long guardianId, RecipientRequestDto reqDto){
        Guardian guardian = guardianRepository.findById(guardianId)
                .orElseThrow(() -> new IllegalArgumentException("돌봄 대상자 정보 생성 실패: 대상 보호자가 없습니다."));
        Recipient recipient = reqDto.toEntity(guardian);
        Recipient created = recipientRepository.save(recipient);
        return RecipientResponseDto.from(created);
    }

    public RecipientResponseDto read(Long id){
        Recipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("돌봄 대상자 조회 실패: 해당 돌봄 대상자가 없습니다."));
        return RecipientResponseDto.from(recipient);
    }

    public List<RecipientResponseDto> readAll(Long guardianId){
        if (!guardianRepository.existsById(guardianId)) {
            throw new IllegalArgumentException("돌봄 대상자 목록 조회 실패: 대상 보호자가 없습니다.");
        }
        return recipientRepository.findByGuardianId(guardianId)
                .stream()
                .map(RecipientResponseDto::from)
                .toList();
    }

    @Transactional
    public RecipientResponseDto update(Long id, RecipientRequestDto reqDto){
        Recipient target = recipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("돌봄 대상자 정보 수정 실패: 해당 돌봄 대상자가 없습니다."));
        target.patch(reqDto); // 보호자 변경은 이 엔드포인트에서 허용하지 않음 (항상 null 전달)
        Recipient updated = recipientRepository.save(target);
        return RecipientResponseDto.from(updated);
    }

    @Transactional
    public RecipientResponseDto delete(Long id){
        Recipient target = recipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("돌봄 대상자 정보 삭제 실패: 해당 돌봄 대상자가 없습니다."));
        recipientRepository.delete(target);
        return RecipientResponseDto.from(target);
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