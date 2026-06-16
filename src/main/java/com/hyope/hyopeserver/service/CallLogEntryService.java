package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.request.CallLogEntryRequestDto;
import com.hyope.hyopeserver.dto.response.CallLogEntryResponseDto;
import com.hyope.hyopeserver.entity.CallLog;
import com.hyope.hyopeserver.entity.CallLogEntry;
import com.hyope.hyopeserver.repository.CallLogEntryRepository;
import com.hyope.hyopeserver.repository.CallLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CallLogEntryService {
    private final CallLogRepository callLogRepository;
    private final CallLogEntryRepository callLogEntryRepository;

    @Transactional
    public CallLogEntryResponseDto create(Long callLogId, CallLogEntryRequestDto reqDto){
        CallLog callLog = callLogRepository.findById(callLogId)
                .orElseThrow(() -> new IllegalArgumentException("통화 기록 생성 실패: 해당 통화 로그가 없습니다."));
        CallLogEntry callLogEntry = reqDto.toEntity(callLog);
        CallLogEntry created = callLogEntryRepository.save(callLogEntry);
        return CallLogEntryResponseDto.from(created);
    }

    //일괄 생성 (bulk create)
    @Transactional
    public List<CallLogEntryResponseDto> createAll(Long callLogId, List<CallLogEntryRequestDto> reqDtos){
        CallLog callLog = callLogRepository.findById(callLogId)
                .orElseThrow(() -> new IllegalArgumentException("통화 기록 생성 실패: 해당 통화 로그가 없습니다."));
        List<CallLogEntry> entries = reqDtos.stream()
                .map(dto -> dto.toEntity(callLog))
                .toList();
        List<CallLogEntry> created = callLogEntryRepository.saveAll(entries);
        return created.stream()
                .map(CallLogEntryResponseDto::from)
                .toList();
    }

    //한 건 조회
    public CallLogEntryResponseDto read(Long id){
        CallLogEntry callLogEntry = callLogEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("통화 기록 조회 실패: 해당 통화 로그가 없습니다."));
        return CallLogEntryResponseDto.from(callLogEntry);
    }

    //모두 조회
    public Page<CallLogEntryResponseDto> readAll(Long callLogId, Pageable pageable){
        if (!callLogRepository.existsById(callLogId)) {
            throw new IllegalArgumentException("통화 기록 목록 조회 실패: 해당 통화 로그가 없습니다.");
        }
        return callLogEntryRepository.findByCallLogId(callLogId, pageable)
                .map(CallLogEntryResponseDto::from);
    }

    //CallLog 단위로 전체 대화(질문-답변) 한 번에 보기
    public List<CallLogEntryResponseDto> readAllByCallLog(Long callLogId){
        if (!callLogRepository.existsById(callLogId)) {
            throw new IllegalArgumentException("통화 기록 목록 조회 실패: 해당 통화 로그가 없습니다.");
        }
        return callLogEntryRepository.findByCallLogIdOrderBySequenceAsc(callLogId)
                .stream()
                .map(CallLogEntryResponseDto::from)
                .toList();
    }

    @Transactional
    public CallLogEntryResponseDto delete(Long id){
        CallLogEntry target = callLogEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("통화 기록 조회 실패: 해당 통화 로그가 없습니다."));
        callLogEntryRepository.delete(target);
        return CallLogEntryResponseDto.from(target);
    }

    @Transactional
    public void deleteAllByCallLogId(Long callLogId){
        callLogEntryRepository.deleteByCallLogId(callLogId);
    }
}
