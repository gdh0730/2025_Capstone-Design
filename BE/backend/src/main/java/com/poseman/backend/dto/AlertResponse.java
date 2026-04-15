package com.poseman.backend.dto;
import lombok.Data;

/**
 * Alert 엔티티를 조회하거나 응답할 때 사용하는 DTO.
 */
@Data
public class AlertResponse {
    private String alertId;    // UUID → 문자열로 반환
    private String eventId;    // 이벤트 ID(UUID) → 문자열
    private String alertType;  // 예: "WebSocket", "Email"
    private String sentTime;   // ISO 문자열 형태, 예: "2025-05-21T14:30:00"
    private int severity;      // 위험도 등급 (1~5)
    private String deliveredTo;// 예: "WEB_CLIENT", "MOBILE_APP"
}
