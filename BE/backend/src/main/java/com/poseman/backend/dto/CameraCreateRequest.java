package com.poseman.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CameraCreateRequest {
    @NotBlank
    private String cameraId;   // 예: "cam1", "cam2" 등
    @NotBlank
    private String storeId;    // 예: "store1", "store2" 등
    private String location;   // 예: "매장 입구", "계산대" 등
}
