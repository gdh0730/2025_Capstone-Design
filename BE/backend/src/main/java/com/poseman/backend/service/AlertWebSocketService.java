package com.poseman.backend.service;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.poseman.backend.dto.AlertMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final Queue<AlertMessage> retryQueue = new LinkedList<>();

    /**
     * WebSocket으로 /topic/alerts 채널에 알림 메시지 전송
     *
     * @param alertMessage - { cameraId, behavior, timestamp, severity } 정보를 포함하는
     *                     DTO
     */
    public void sendAlert(AlertMessage alertMessage) {
        try {
            messagingTemplate.convertAndSend("/topic/alerts", alertMessage);
        } catch (MessagingException ex) {
            // 예외 처리: 메시지 전송 실패 시 로깅 + 재시도 큐 넣기 등 추가 로직 필요
            Logger.getLogger(AlertWebSocketService.class.getName())
                    .severe("Failed to send alert message: " + ex.getMessage());
            // 예외 발생 시 추가적인 로직을 여기에 구현할 수 있습니다.
            // 예를 들어, 재시도 큐에 메시지를 넣거나, 다른 알림 시스템으로 전송하는 등의 작업을 수행할 수 있습니다.
            // 예시: 재시도 큐에 메시지 추가
            retryQueue.add(alertMessage);
        }
    }

    /**
     * 3초마다 재시도 큐에 쌓인 메시지를 다시 전송 시도.
     * 
     * @Scheduled(fixedDelay = 10000) → 메시지 전송 후 최소 10초 이후에 다시 호출
     */
    @Scheduled(fixedDelay = 3000, initialDelay = 5000)
    public void retryFailedAlerts() {
        if (retryQueue.isEmpty()) {
            return;
        }

        // '현재 큐에 쌓인 모든 메시지'를 잠깐 뽑아서 처리
        int queueSize = retryQueue.size();
        for (int i = 0; i < queueSize; i++) {
            AlertMessage message = retryQueue.poll();
            if (message == null) {
                break;
            }
            try {
                messagingTemplate.convertAndSend("/topic/alerts", message);
                // 재전송 성공 시 그냥 넘김 (큐에서 이미 꺼냈으므로 제거 완료)
            } catch (MessagingException ex) {
                // 재전송 실패 → 다시 큐에 넣고, 로그만 남김
                Logger.getLogger(AlertWebSocketService.class.getName())
                        .severe("Retry failed for alert message: " + ex.getMessage());
                retryQueue.add(message);
            }
        }
    }
}
