package com.hyope.hyopeserver.controller;

import com.hyope.hyopeserver.dto.request.CallScheduleRequestDto;
import com.hyope.hyopeserver.dto.response.CallScheduleResponseDto;
import com.hyope.hyopeserver.service.CallScheduleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class CallScheduleController {
    private final CallScheduleService callScheduleService;
    //GET
    @GetMapping("/call-schedule/{id}")
    public ResponseEntity<CallScheduleResponseDto> read(@PathVariable Long id){
        return ResponseEntity.ok(callScheduleService.read(id));
    }
    @GetMapping("/recipient/{recipientId}/call-schedule")
    public ResponseEntity<List<CallScheduleResponseDto>> readAll(@PathVariable Long recipientId){
        return ResponseEntity.ok(callScheduleService.readAll(recipientId));
    }
    //POST
    @PostMapping("/recipient/{recipientId}/call-schedule")
    public ResponseEntity<CallScheduleResponseDto> create(@PathVariable Long recipientId, @RequestBody CallScheduleRequestDto reqDto){
        CallScheduleResponseDto created = callScheduleService.create(recipientId, reqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    //PATCH
    @PatchMapping("/call-schedule/{id}")
    public ResponseEntity<CallScheduleResponseDto> update(@PathVariable Long id, @RequestBody CallScheduleRequestDto reqDto){
        CallScheduleResponseDto updated = callScheduleService.update(id, reqDto);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }
    //DELETE
    @DeleteMapping("/call-schedule/{id}")
    public ResponseEntity<CallScheduleResponseDto> delete(@PathVariable Long id){
        CallScheduleResponseDto deleted = callScheduleService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(deleted);
    }
}
