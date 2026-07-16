package com.hyope.hyopeserver.controller;

import com.hyope.hyopeserver.dto.calllogentry.CallLogEntryRequest;
import com.hyope.hyopeserver.dto.calllogentry.CallLogEntryResponse;
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
    @GetMapping("/call-log-entries/{id}")
    public ResponseEntity<CallLogEntryResponse> read(@PathVariable Long id){
        return ResponseEntity.ok(callLogEntryService.read(id));
    }
    @GetMapping("/call-logs/{callLogId}/call-log-entries")
    public ResponseEntity<Page<CallLogEntryResponse>> readAll(@PathVariable Long callLogId,
                                                              @PageableDefault(size = 20, sort = "sequence") Pageable pageable){
        return ResponseEntity.ok(callLogEntryService.readAll(callLogId, pageable));
    }
    //POST
    @PostMapping("/call-logs/{callLogId}/call-log-entries")
    public ResponseEntity<List<CallLogEntryResponse>> createAll(@PathVariable Long callLogId,
                                                                @RequestBody List<CallLogEntryRequest> reqDtos){
        List<CallLogEntryResponse> created = callLogEntryService.createAll(callLogId, reqDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    //DELETE
    @DeleteMapping("/call-log-entries/{id}")
    public ResponseEntity<CallLogEntryResponse> delete(@PathVariable Long id){
        CallLogEntryResponse deleted = callLogEntryService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(deleted);
    }
}
