package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.request.CallLogRequestDto;
import com.hyope.hyopeserver.dto.response.CallLogResponseDto;
import com.hyope.hyopeserver.entity.CallLog;
import com.hyope.hyopeserver.entity.Recipient;
import com.hyope.hyopeserver.entity.enums.Status;
import com.hyope.hyopeserver.repository.CallLogRepository;
import com.hyope.hyopeserver.repository.RecipientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CallLogService {
    private final RecipientRepository recipientRepository;
    private final CallLogRepository callLogRepository;
    private final CallLogEntryService callLogEntryService;
    private final AssessmentService assessmentService;

    @Transactional
    public CallLogResponseDto create(Long recipientId, CallLogRequestDto reqDto){
        Recipient recipient = recipientRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("통화 로그 정보 생성 실패: 해당 돌봄 대상자가 없습니다."));
        CallLog callLog = reqDto.toEntity(recipient);
        CallLog created = callLogRepository.save(callLog);
        return CallLogResponseDto.createCallLogResponseDto(created);
    }

    //한 건 조회
    public CallLogResponseDto read(Long id){
        CallLog callLog = callLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("통화 로그 조회 실패: 해당 돌봄 대상자가 없습니다."));
        return CallLogResponseDto.createCallLogResponseDto(callLog);
    }

    //모두 조회
    public Page<CallLogResponseDto> readAll(Long recipientId, Pageable pageable){
        return callLogRepository.findByRecipientId(recipientId, pageable)
                .map(CallLogResponseDto::createCallLogResponseDto);
    }

    //날짜별 조회
    public List<CallLogResponseDto> readByDateRange(Long recipientId, LocalDateTime from, LocalDateTime to){
        if (!recipientRepository.existsById(recipientId)) {
            throw new IllegalArgumentException("통화 로그 목록 조회 실패: 해당 돌봄 대상자가 없습니다.");
        }
        return callLogRepository.findByRecipientIdAndStartedAtBetween(recipientId, from, to)
                .stream()
                .map(CallLogResponseDto::createCallLogResponseDto)
                .toList();
    }

    //상태별 조회
    public List<CallLogResponseDto> readByStatus(Long recipientId, Status status){
        return callLogRepository.findByRecipientIdAndStatus(recipientId, status)
                .stream()                                    // List -> Stream 변환
                .map(CallLogResponseDto::createCallLogResponseDto)
                .toList();
    }

    //최근 건 조회
    public CallLogResponseDto readLatest(Long recipientId){
        CallLog callLog = callLogRepository.findFirstByRecipientIdOrderByStartedAtDesc(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("통화 로그 조회 실패: 기록이 없습니다."));
        return CallLogResponseDto.createCallLogResponseDto(callLog);
    }

    @Transactional
    public CallLogResponseDto delete(Long id){
        CallLog target = callLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("통화 로그 삭제 실패: 해당 통화 로그가 없습니다."));
        callLogRepository.delete(target);
        return CallLogResponseDto.createCallLogResponseDto(target);
    }

    @Transactional
    public void deleteAllByRecipientId(Long recipientId){
        List<CallLog> logs = callLogRepository.findByRecipientId(recipientId);
        for (CallLog log : logs) {
            callLogEntryService.deleteAllByCallLogId(log.getId());
            assessmentService.deleteByCallLogId(log.getId());  // 추가
        }
        callLogRepository.deleteByRecipientId(recipientId);
    }
}
