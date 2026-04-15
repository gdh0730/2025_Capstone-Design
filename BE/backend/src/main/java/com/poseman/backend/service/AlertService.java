package com.poseman.backend.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poseman.backend.domain.entity.Alert;
import com.poseman.backend.domain.entity.EventLog;
import com.poseman.backend.domain.repository.AlertRepository;
import com.poseman.backend.domain.repository.EventLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final EventLogRepository eventLogRepository;

    /**
     * 이벤트 ID와 알림 유형을 받아, 데이터베이스에 Alert 엔티티를 저장한다.
     * 매뉴얼에 따라 severity와 deliveredTo 필드를 반드시 세팅해야 함.
     *
     * @param eventId   - 저장할 이벤트의 고유 ID(UUID 문자열)
     * @param alertType - “WebSocket”, “Email” 등 알림 타입
     */
    @Transactional
    public void sendAlert(String eventId, String alertType) {
        // 1) EventLog 엔티티 조회 (N:1 매핑)
        java.util.UUID eventUUID = java.util.UUID.fromString(eventId);

        Alert existing = alertRepository.findByEvent_EventId(eventUUID);
        if (existing != null) {
            // 이미 해당 이벤트에 대한 Alert가 존재하면 넘어가기
            return;
        } else {
            EventLog event = eventLogRepository.findById(eventUUID)
                    .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

            // 2) Alert 엔티티 생성
            Alert alert = new Alert();
            // Alert 엔티티 PK는 @GeneratedValue로 자동 생성된다면 직접 설정할 필요 없음
            alert.setEvent(event);

            // 매뉴얼에 정의된 추가 필드들 반드시 세팅
            alert.setAlertType(alertType);
            alert.setSentTime(LocalDateTime.now());
            // severity는 EventLog에 저장된 위험도 등급을 그대로 상속
            alert.setSeverity(event.getSeverity());
            // deliveredTo는 알림을 받을 주체(“WEB_CLIENT”, “MOBILE_APP” 등) 설정
            alert.setDeliveredTo("WEB_CLIENT");

            // 3) DB에 저장
            alertRepository.save(alert);
        }
    }
}
