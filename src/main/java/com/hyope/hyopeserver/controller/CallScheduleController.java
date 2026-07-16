package com.hyope.hyopeserver.controller;

import com.hyope.hyopeserver.dto.callschedule.CallScheduleRequest;
import com.hyope.hyopeserver.dto.callschedule.CallScheduleResponse;
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
    @GetMapping("/call-schedules/{id}")
    public ResponseEntity<CallScheduleResponse> read(@PathVariable Long id){
        return ResponseEntity.ok(callScheduleService.read(id));
    }
    @GetMapping("/recipients/{recipientId}/call-schedules")
    public ResponseEntity<List<CallScheduleResponse>> readAll(@PathVariable Long recipientId){
        return ResponseEntity.ok(callScheduleService.readAll(recipientId));
    }
    //POST
    @PostMapping("/recipients/{recipientId}/call-schedules")
    public ResponseEntity<CallScheduleResponse> create(@PathVariable Long recipientId, @RequestBody CallScheduleRequest reqDto){
        CallScheduleResponse created = callScheduleService.create(recipientId, reqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    //PATCH
    @PatchMapping("/call-schedules/{id}")
    public ResponseEntity<CallScheduleResponse> update(@PathVariable Long id, @RequestBody CallScheduleRequest reqDto){
        CallScheduleResponse updated = callScheduleService.update(id, reqDto);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }
    //DELETE
    @DeleteMapping("/call-schedules/{id}")
    public ResponseEntity<CallScheduleResponse> delete(@PathVariable Long id){
        CallScheduleResponse deleted = callScheduleService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(deleted);
    }
}
