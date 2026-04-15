package com.poseman.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CameraResponse {
    private String cameraId;
    private String storeId;
    private String location;
    private boolean status;
    private String createdAt;   // ISO 문자열
}
