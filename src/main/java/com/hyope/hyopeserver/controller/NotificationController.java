package com.hyope.hyopeserver.controller;

import com.hyope.hyopeserver.dto.notification.NotificationResponse;
import com.hyope.hyopeserver.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationResponse>> readMine(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(notificationService.readAllByLoginId(userDetails.getUsername()));
    }

    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return ResponseEntity.ok().build();
    }
}