package com.hyope.hyopeserver.controller;

import com.hyope.hyopeserver.dto.emergencyalert.EmergencyAlertRequest;
import com.hyope.hyopeserver.dto.emergencyalert.EmergencyAlertResponse;
import com.hyope.hyopeserver.service.EmergencyAlertService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@AllArgsConstructor
public class EmergencyAlertController {

    private final EmergencyAlertService emergencyAlertService;

    @PostMapping("/internal/emergency-alerts")
    public ResponseEntity<EmergencyAlertResponse> receiveEmergencyAlert(@RequestBody EmergencyAlertRequest dto) {
        log.info("위급 알림 수신 - recipientId={}, severity={}", dto.getRecipientId(), dto.getSeverity());
        return ResponseEntity.ok(emergencyAlertService.handle(dto));
    }
}
