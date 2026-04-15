package com.poseman.backend.websocket;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.poseman.backend.security.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * WebSocket(STOMP) 연결 시 클라이언트가 URL 파라미터 또는 헤더에 실어 보낸 JWT 토큰을 검증하는 인터셉터.
 */
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    /**
     * WebSocket 핸드쉐이크 요청 직전에 호출.
     * - URL 파라미터(token) 또는 헤더(Authorization: Bearer ...)를 통해 JWT를 꺼내고,
     * 파싱이 성공하면 세션 속성(attributes)에 username을 저장.
     * - 파싱에 실패하거나 토큰이 없는 경우 false를 반환해 연결을 거부.
     */
    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler handler, @NonNull Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            // 1) URL 파라미터로 token 꺼내기
            String token = httpRequest.getParameter("token");

            // 2) header에서 Authorization: Bearer <token> 꺼내기
            if (token == null) {
                String authHeader = httpRequest.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
            }

            if (token != null) {
                try {
                    // 토큰 파싱
                    Claims claims = jwtUtil.extractClaims(token);
                    // session(attributes)에 username(또는 userId)을 남겨두면 추후 메시지 핸들러에서 참조 가능
                    attributes.put("username", claims.getSubject());
                    // 필요하다면 userId도 저장할 수 있음
                    attributes.put("userId", claims.get("userId", String.class));
                    return true;
                } catch (Exception e) {
                    // 만료됐거나 위조된 토큰 등
                    return false;
                }
            }
        }
        // token 파라미터/헤더가 모두 없으면 연결 거부
        return false;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler, @Nullable Exception exception) {
        // Handshake 후 별도 처리 불필요
    }
}
