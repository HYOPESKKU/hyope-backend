package com.hyope.hyopeserver.websocket;

import com.hyope.hyopeserver.entity.Guardian;
import com.hyope.hyopeserver.service.GuardianService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * /ws/notifications 핸드셰이크는 일반 HTTP GET으로 시큐리티 필터 체인(JwtAuthFilter 포함)을
 * 그대로 통과한 뒤 여기 도달한다. 그래서 SecurityContextHolder에 이미 인증 정보가 있으면
 * 그걸로 guardianId를 찾아 세션 attribute에 심어둔다. 인증이 없으면 핸드셰이크를 거부한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationHandshakeInterceptor implements HandshakeInterceptor {

    private final GuardianService guardianService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                    WebSocketHandler wsHandler, Map<String, Object> attributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        try {
            Guardian guardian = guardianService.readByLoginId(authentication.getName());
            attributes.put("guardianId", guardian.getId());
            return true;
        } catch (IllegalArgumentException e) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                WebSocketHandler wsHandler, Exception exception) {
    }
}