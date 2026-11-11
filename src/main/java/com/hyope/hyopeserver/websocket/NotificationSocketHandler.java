package com.hyope.hyopeserver.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * guardianId 하나당 여러 기기/탭이 붙을 수 있어 세션을 Set으로 들고 있는다.
 * 알림이 생성되는 지점(NotificationService)에서 push()를 호출하면 연결돼 있는
 * 모든 세션에 즉시 JSON을 밀어준다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final Map<Long, Set<WebSocketSession>> sessionsByGuardian = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long guardianId = guardianIdOf(session);
        if (guardianId == null) {
            closeQuietly(session, CloseStatus.POLICY_VIOLATION);
            return;
        }
        sessionsByGuardian.computeIfAbsent(guardianId, id -> new CopyOnWriteArraySet<>()).add(session);
        log.info("Notification socket connected: guardianId={}", guardianId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long guardianId = guardianIdOf(session);
        if (guardianId == null) return;
        Set<WebSocketSession> sessions = sessionsByGuardian.get(guardianId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                sessionsByGuardian.remove(guardianId);
            }
        }
    }

    public void push(Long guardianId, Object payload) {
        Set<WebSocketSession> sessions = sessionsByGuardian.get(guardianId);
        if (sessions == null || sessions.isEmpty()) return;

        String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (IOException e) {
            log.warn("Failed to serialize notification push payload", e);
            return;
        }

        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                }
            } catch (IOException e) {
                log.warn("Failed to push notification to guardianId={}", guardianId, e);
            }
        }
    }

    private Long guardianIdOf(WebSocketSession session) {
        Object value = session.getAttributes().get("guardianId");
        return value instanceof Long ? (Long) value : null;
    }

    private void closeQuietly(WebSocketSession session, CloseStatus status) {
        try {
            session.close(status);
        } catch (IOException ignored) {
        }
    }
}