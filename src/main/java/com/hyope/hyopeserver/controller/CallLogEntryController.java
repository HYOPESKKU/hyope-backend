package com.hyope.hyopeserver.controller;

import com.hyope.hyopeserver.dto.request.CallLogEntryRequestDto;
import com.hyope.hyopeserver.dto.response.CallLogEntryResponseDto;
import com.hyope.hyopeserver.service.CallLogEntryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class CallLogEntryController {
    private final CallLogEntryService callLogEntryService;
    //GET
    @GetMapping("/call-log-entry/{id}")
    public ResponseEntity<CallLogEntryResponseDto> read(@PathVariable Long id){
        return ResponseEntity.ok(callLogEntryService.read(id));
    }
    @GetMapping("/call-log/{callLogId}/call-log-entry")
    public ResponseEntity<Page<CallLogEntryResponseDto>> readAll(@PathVariable Long callLogId,
                                                                 @PageableDefault(size = 20, sort = "sequence") Pageable pageable){
        return ResponseEntity.ok(callLogEntryService.readAll(callLogId, pageable));
    }
    @GetMapping("/call-log/{callLogId}/call-log-entry/all")
    public ResponseEntity<List<CallLogEntryResponseDto>> readAllByCallLog(@PathVariable Long callLogId){
        return ResponseEntity.ok(callLogEntryService.readAllByCallLog(callLogId));
    }
    //POST
    @PostMapping("/call-log/{callLogId}/call-log-entry")
    public ResponseEntity<CallLogEntryResponseDto> create(@PathVariable Long callLogId,
                                                          @RequestBody CallLogEntryRequestDto reqDto){
        CallLogEntryResponseDto created = callLogEntryService.create(callLogId, reqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    @PostMapping("/call-log/{callLogId}/call-log-entry/bulk")
    public ResponseEntity<List<CallLogEntryResponseDto>> createAll(@PathVariable Long callLogId,
                                                                   @RequestBody List<CallLogEntryRequestDto> reqDtos){
        List<CallLogEntryResponseDto> created = callLogEntryService.createAll(callLogId, reqDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    //DELETE
    @DeleteMapping("/call-log-entry/{id}")
    public ResponseEntity<CallLogEntryResponseDto> delete(@PathVariable Long id){
        CallLogEntryResponseDto deleted = callLogEntryService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(deleted);
    }
}
