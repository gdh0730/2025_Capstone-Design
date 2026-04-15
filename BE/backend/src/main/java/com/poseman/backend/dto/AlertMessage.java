package com.poseman.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket 메시지를 보낼 때 사용하는 DTO.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertMessage {
    private String cameraId;   // 예: "cam3"
    private String behavior;   // 예: "stealing"
    private String timestamp;  // ISO 형태 문자열, 예: "2025-05-21T14:30:00"
    private String severity;   // 위험도 등급을 문자열로 전달 (예: "3")
}
