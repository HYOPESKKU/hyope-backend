package com.hyope.hyopeserver.controller;

import com.hyope.hyopeserver.dto.notification.NotificationResponse;
import com.hyope.hyopeserver.entity.Guardian;
import com.hyope.hyopeserver.entity.GuardianNotification;
import com.hyope.hyopeserver.service.GuardianService;
import com.hyope.hyopeserver.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final GuardianService guardianService;

    @GetMapping("/notifications")
    public List<NotificationResponse> readMine(Authentication authentication) {
        Guardian guardian = guardianService.readByLoginId(authentication.getName());
        return notificationService.readAllForGuardian(guardian.getId()).stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<NotificationResponse> markRead(@PathVariable Long id, Authentication authentication) {
        Guardian guardian = guardianService.readByLoginId(authentication.getName());
        GuardianNotification updated = notificationService.markRead(id, guardian.getId());
        return ResponseEntity.ok(NotificationResponse.from(updated));
    }

    @DeleteMapping("/notifications/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        Guardian guardian = guardianService.readByLoginId(authentication.getName());
        notificationService.delete(id, guardian.getId());
        return ResponseEntity.noContent().build();
    }
}