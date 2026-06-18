package com.hyope.hyopeserver.controller;

import com.hyope.hyopeserver.dto.request.AiCallResultDto;
import com.hyope.hyopeserver.service.CallResultService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@AllArgsConstructor
public class CallResultController {

    private final CallResultService callResultService;

    @PostMapping("/call-result")
    public ResponseEntity<Void> receiveCallResult(@RequestBody AiCallResultDto dto) {
        log.info("AI 분석 결과 수신 - recipientId={}", dto.getRecipientId());
        callResultService.saveAll(dto);
        return ResponseEntity.ok().build();
    }
}