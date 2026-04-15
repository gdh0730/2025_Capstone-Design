package com.poseman.backend.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 클라이언트(예: FastAPI)에서 POST /api/events 로 보낼 때 사용하는 DTO.
 */
@Data
public class EventCreateRequest {
    @NotNull
    private LocalDateTime timestamp;  // 예: LocalDateTime.parse("2025-05-21T14:30:00")
    @NotBlank
    private String cameraId;          // 예: "cam3"
    @NotBlank
    private String behavior;          // 예: "stealing"
    @Min(0)
    @Max(1)
    private float confidence;         // 예: 0.87f
    /*
    @Min(1)
    @Max(5)
    private int severity;             // 예: 3
    */
}
