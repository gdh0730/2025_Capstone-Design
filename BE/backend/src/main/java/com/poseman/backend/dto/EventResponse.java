package com.poseman.backend.dto;
import lombok.Builder;
import lombok.Data;

/**
 * 백엔드에서 /api/events GET 요청 시 응답 형태로 사용하는 DTO.
 */
@Data
@Builder
public class EventResponse {
    private String eventId;    // UUID → 문자열
    private String cameraId;   // 예: "cam3"
    private String behavior;   // 예: "stealing"
    private float confidence;  // 예: 0.87f
    private int severity;      // 예: 3
    private String timestamp;  // ISO 문자열 형태, 예: "2025-05-21T14:30:00"
}
