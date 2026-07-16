package com.hyope.hyopeserver.controller;

import com.hyope.hyopeserver.dto.calllog.CallLogRequest;
import com.hyope.hyopeserver.dto.calllog.CallLogResponse;
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
    @GetMapping("/call-logs/{id}") //한 건 조회
    public ResponseEntity<CallLogResponse> read(@PathVariable Long id){
        return ResponseEntity.ok(callLogService.read(id));
    }
    @GetMapping("/recipients/{recipientId}/call-logs") //모두 조회
    public ResponseEntity<Page<CallLogResponse>> readAll(@PathVariable Long recipientId,
                                                         @PageableDefault(size = 20, sort = "startedAt") Pageable pageable){
        return ResponseEntity.ok(callLogService.readAll(recipientId, pageable));
    }
    @GetMapping("/recipients/{recipientId}/call-logs/by-date") //날짜별 조회
    public ResponseEntity<List<CallLogResponse>> readByDateRange(@PathVariable Long recipientId,
                                                                 @RequestParam LocalDateTime from,
                                                                 @RequestParam LocalDateTime to){
        return ResponseEntity.ok(callLogService.readByDateRange(recipientId, from, to));
    }
    @GetMapping("/recipients/{recipientId}/call-logs/by-status") //상태별 조회
    public ResponseEntity<List<CallLogResponse>> readByStatus(@PathVariable Long recipientId,
                                                              @RequestParam Status status){
        return ResponseEntity.ok(callLogService.readByStatus(recipientId, status));
    }
    //POST
    @PostMapping("/recipients/{recipientId}/call-logs")
    public ResponseEntity<CallLogResponse> create(@PathVariable Long recipientId,
                                                  @RequestBody CallLogRequest reqDto){
        CallLogResponse created = callLogService.create(recipientId, reqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    //DELETE
    @DeleteMapping("/call-logs/{id}")
    public ResponseEntity<CallLogResponse> delete(@PathVariable Long id){
        CallLogResponse deleted = callLogService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(deleted);
    }
}
