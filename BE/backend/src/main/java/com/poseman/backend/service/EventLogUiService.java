package com.poseman.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.poseman.backend.domain.entity.EventLog;
import com.poseman.backend.domain.repository.EventLogRepository;
import com.poseman.backend.dto.EventResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventLogUiService {

    private final EventLogRepository eventLogRepository;
    // private final CameraRepository cameraRepository;

    /**
     * EventCreateRequest를 받아
     * 1) Camera 엔티티 유효성 체크
     * 2) 새로운 EventLog 엔티티 생성 후 저장
     * 3) 저장된 데이터를 EventResponse DTO로 반환
     *
     * @param request - { timestamp, cameraId, behavior, confidence, severity }
     * @return EventResponse
     */
    /*
     * public EventResponse saveEvent(EventCreateRequest request) {
     * // 1) cameraId가 존재하는지 확인
     * Camera camera = cameraRepository.findById(request.getCameraId())
     * .orElseThrow(() -> new IllegalArgumentException("Camera not found: " +
     * request.getCameraId()));
     * 
     * // 2) EventLog 엔티티 생성 및 매핑
     * EventLog event = new EventLog();
     * // 만약 @GeneratedValue로 ID를 자동 생성하면 다음 줄은 생략 가능
     * // event.setEventId(UUID.randomUUID());
     * event.setCamera(camera);
     * event.setTimestamp(request.getTimestamp());
     * event.setBehavior(request.getBehavior());
     * event.setConfidence(request.getConfidence());
     * event.setSeverity(calculateSeverity(request.getConfidence()));
     * 
     * // 3) DB에 저장
     * EventLog saved = eventLogRepository.save(event);
     * 
     * // 4) Entity → DTO 변환하여 반환
     * return toResponse(saved);
     * }
     */

    // 1) 모든 이벤트 로그를 조회합니다.
    /**
     * 모든 이벤트 로그를 조회합니다.
     *
     * @return List<EventResponse> - 모든 이벤트 로그 리스트
     */
    public List<EventResponse> getAllEvents() {
        List<EventLog> events = eventLogRepository.findAll();
        return events.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * 특정 날짜의 이벤트 로그를 조회합니다.
     * - cameraId가 null 또는 빈 문자열인 경우 모든 카메라의 이벤트를 조회
     * - cameraId가 유효한 경우 해당 카메라의 이벤트만 조회
     *
     * @param date     - 조회할 날짜 (LocalDate)
     * @param cameraId - 카메라 ID (null 또는 빈 문자열 가능)
     * @return List<EventResponse> - 해당 날짜의 이벤트 로그 리스트
     */
    public List<EventResponse> getEventsByDate(LocalDate date, String cameraId) {
        LocalDateTime start = date.atStartOfDay(); // 00:00:00
        LocalDateTime end = date.atTime(23, 59, 59); // 23:59:59

        List<EventLog> events;
        if (cameraId == null || cameraId.isBlank()) {
            events = eventLogRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);
        } else {
            events = eventLogRepository.findByCamera_CameraIdAndTimestampBetweenOrderByTimestampDesc(cameraId, start,
                    end);
        }
        return events.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /** EventLog → EventResponse DTO 변환 헬퍼 메서드 */
    private EventResponse toResponse(EventLog e) {
        return EventResponse.builder()
                .eventId(e.getEventId().toString())
                .cameraId(e.getCamera().getCameraId())
                .behavior(e.getBehavior())
                .confidence(e.getConfidence())
                .severity(e.getSeverity())
                .timestamp(e.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }
}
