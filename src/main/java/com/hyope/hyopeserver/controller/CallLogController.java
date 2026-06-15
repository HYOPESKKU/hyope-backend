package com.hyope.hyopeserver.controller;

import com.hyope.hyopeserver.dto.request.CallLogRequestDto;
import com.hyope.hyopeserver.dto.response.CallLogResponseDto;
import com.hyope.hyopeserver.entity.enums.Status;
import com.hyope.hyopeserver.service.CallLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class CallLogController {
    private final CallLogService callLogService;
    //GET
    @GetMapping("/call-log/{id}") //한 건 조회
    public ResponseEntity<CallLogResponseDto> read(@PathVariable Long id){
        return ResponseEntity.ok(callLogService.read(id));
    }
    @GetMapping("/recipient/{recipientId}/call-log") //모두 조회
    public ResponseEntity<Page<CallLogResponseDto>> readAll(@PathVariable Long recipientId,
                                                            @PageableDefault(size = 20, sort = "startedAt") Pageable pageable){
        return ResponseEntity.ok(callLogService.readAll(recipientId, pageable));
    }
    @GetMapping("/recipient/{recipientId}/call-log/by-date") //날짜별 조회
    public ResponseEntity<List<CallLogResponseDto>> readByDateRange(@PathVariable Long recipientId,
                                                                    @RequestParam LocalDateTime from,
                                                                    @RequestParam LocalDateTime to){
        return ResponseEntity.ok(callLogService.readByDateRange(recipientId, from, to));
    }
    @GetMapping("/recipient/{recipientId}/call-log/by-status") //상태별 조회
    public ResponseEntity<List<CallLogResponseDto>> readByStatus(@PathVariable Long recipientId,
                                                                 @RequestParam Status status){
        return ResponseEntity.ok(callLogService.readByStatus(recipientId, status));
    }
    //POST
    @PostMapping("/recipient/{recipientId}/call-log")
    public ResponseEntity<CallLogResponseDto> create(@PathVariable Long recipientId,
                                                     @RequestBody CallLogRequestDto reqDto){
        CallLogResponseDto created = callLogService.create(recipientId, reqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    //DELETE
    @DeleteMapping("/call-log/{id}")
    public ResponseEntity<CallLogResponseDto> delete(@PathVariable Long id){
        CallLogResponseDto deleted = callLogService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(deleted);
    }
}
