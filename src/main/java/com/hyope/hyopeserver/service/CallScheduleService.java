package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.callschedule.CallScheduleRequest;
import com.hyope.hyopeserver.dto.callschedule.CallScheduleResponse;
import com.hyope.hyopeserver.entity.CallSchedule;
import com.hyope.hyopeserver.entity.Recipient;
import com.hyope.hyopeserver.repository.CallScheduleRepository;
import com.hyope.hyopeserver.repository.RecipientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CallScheduleService {
    private final RecipientRepository recipientRepository;
    private final CallScheduleRepository callScheduleRepository;

    @Transactional
    public CallScheduleResponse create(Long recipientId, CallScheduleRequest reqDto){
        Recipient recipient = recipientRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("통화 스케줄 생성 실패: 돌봄 대상자가 없습니다."));
        CallSchedule callSchedule = reqDto.toEntity(recipient);
        CallSchedule created = callScheduleRepository.save(callSchedule);
        return CallScheduleResponse.from(created);
    }

    public CallScheduleResponse read(Long id){
        CallSchedule callSchedule = callScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("통화 스케줄 조회 실패: 해당 통화 스케줄이 없습니다."));
        return CallScheduleResponse.from(callSchedule);
    }

    public List<CallScheduleResponse> readAll(Long recipientId){
        if (!recipientRepository.existsById(recipientId)) {
            throw new IllegalArgumentException("통화 스케줄 목록 조회 실패: 돌봄 대상자가 없습니다.");
        }
        return callScheduleRepository.findByRecipientId(recipientId)
                .stream()
                .map(CallScheduleResponse::from)
                .toList();
    }

    @Transactional
    public CallScheduleResponse update(Long id, CallScheduleRequest reqDto){
        CallSchedule target = callScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("통화 스케줄 수정 실패: 해당 통화 스케줄이 없습니다."));

        Recipient recipient = null;
        if (reqDto.getRecipientId() != null) {
            recipient = recipientRepository.findById(reqDto.getRecipientId())
                    .orElseThrow(() -> new IllegalArgumentException("통화 스케줄 수정 실패: 돌봄 대상자가 없습니다."));
        }

        target.patch(reqDto, recipient);
        CallSchedule updated = callScheduleRepository.save(target);
        return CallScheduleResponse.from(updated);
    }

    @Transactional
    public CallScheduleResponse delete(Long id){
        CallSchedule target = callScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("통화 스케줄 삭제 실패: 해당 통화 스케줄이 없습니다."));
        callScheduleRepository.delete(target);
        return CallScheduleResponse.from(target);
    }

    @Transactional
    public void deleteAllByRecipientId(Long recipientId){
        callScheduleRepository.deleteByRecipientId(recipientId);
    }
}
