package com.poseman.backend.service;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.poseman.backend.domain.entity.Camera;
import com.poseman.backend.domain.entity.EventLog;
import com.poseman.backend.domain.repository.CameraRepository;
import com.poseman.backend.domain.repository.EventLogRepository;
import com.poseman.backend.dto.AlertMessage;
import com.poseman.backend.dto.EventCreateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventLogRepository eventLogRepository;
    private final CameraRepository cameraRepository;
    private final AlertWebSocketService alertWebSocketService;

    /**
     * FastAPI 중계 서버가 POST /api/events를 호출할 때 사용.
     * 1) EventCreateRequest → EventLog 엔티티로 매핑 후 저장
     * 2) 저장된 EventLog로 WebSocket 실시간 알림 전송
     *
     * @param request - { timestamp, cameraId, behavior, confidence }
     * @return EventResponse
     */
    public void saveEvent(EventCreateRequest request) {
        // 1) Camera 엔티티 확인 (N:1 매핑)
        Camera camera = cameraRepository.findById(request.getCameraId())
                .orElseThrow(() -> new IllegalArgumentException("Camera not found: " + request.getCameraId()));

        // 2) EventLog 엔티티 생성
        EventLog event = new EventLog();
        event.setCamera(camera);
        event.setTimestamp(request.getTimestamp());
        event.setBehavior(request.getBehavior());
        event.setConfidence(request.getConfidence());
        // severity 계산 로직 (매뉴얼 기준)
        event.setSeverity(calculateSeverity(request.getConfidence()));

        // 3) DB에 저장
        eventLogRepository.save(event);

        // 4) WebSocket 메시지 생성 및 전송
        AlertMessage alertMessage = new AlertMessage(
                camera.getCameraId(),
                request.getBehavior(),
                request.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                String.valueOf(calculateSeverity(request.getConfidence())));
        alertWebSocketService.sendAlert(alertMessage);
    }

    /** confidence별 severity 레벨 계산 (매뉴얼 대로) */
    private int calculateSeverity(float confidence) {
        if (confidence >= 0.9) {
            return 5;
        } else if (confidence >= 0.7) {
            return 3;
        } else {
            return 1;
        }
    }
}
