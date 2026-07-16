package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.calllog.CallLogRequest;
import com.hyope.hyopeserver.dto.calllog.CallLogResponse;
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
    public CallLogResponse create(Long recipientId, CallLogRequest reqDto){
        Recipient recipient = recipientRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("통화 로그 정보 생성 실패: 해당 돌봄 대상자가 없습니다."));
        CallLog callLog = reqDto.toEntity(recipient);
        CallLog created = callLogRepository.save(callLog);
        return CallLogResponse.from(created);
    }

    //한 건 조회
    public CallLogResponse read(Long id){
        CallLog callLog = callLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("통화 로그 조회 실패: 해당 통화 로그가 없습니다."));
        return CallLogResponse.from(callLog);
    }

    //조건별(옵션) + 페이징 조회
    public Page<CallLogResponse> readAll(Long recipientId, LocalDateTime from, LocalDateTime to,
                                         Status status, Pageable pageable){
        if (!recipientRepository.existsById(recipientId)) {
            throw new IllegalArgumentException("통화 로그 목록 조회 실패: 해당 돌봄 대상자가 없습니다.");
        }
        return callLogRepository.findAllByFilters(recipientId, from, to, status, pageable)
                .map(CallLogResponse::from);
    }

    @Transactional
    public CallLogResponse delete(Long id){
        CallLog target = callLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("통화 로그 삭제 실패: 해당 통화 로그가 없습니다."));
        callLogRepository.delete(target);
        return CallLogResponse.from(target);
    }

    @Transactional
    public void deleteAllByRecipientId(Long recipientId){
        List<CallLog> logs = callLogRepository.findByRecipientId(recipientId);
        for (CallLog log : logs) {
            callLogEntryService.deleteAllByCallLogId(log.getId());
            assessmentService.deleteByCallLogId(log.getId());
        }
        callLogRepository.deleteByRecipientId(recipientId);
    }
}