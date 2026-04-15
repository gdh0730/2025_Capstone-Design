package com.poseman.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.poseman.backend.websocket.JwtHandshakeInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
    private final AppProperties appProperties;

    /**
     * 메시지 브로커 설정: "/topic" 경로로 발송된 메시지는 SimpleBroker가 관리
     * "/app" 경로로 들어오는 메시지는 @MessageMapping이 처리
     */
    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // 클라이언트가 구독할 토픽 prefix
        config.setApplicationDestinationPrefixes("/app"); // 클라이언트가 메시지 보낼 때 prefix
    }

    /**
     * STOMP 엔드포인트 등록:
     * "/ws/alert" 로 SockJS 또는 WebSocket 연결을 허용
     * JwtHandshakeInterceptor를 통해 인증(토큰 검증) 로직 수행
     */
    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/alert")
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOriginPatterns(appProperties.getFrontendUrl()) // 개발 중에는 * 허용, 운영 시 제한 권장
                .withSockJS(); // SockJS fallback 지원
    }
}
