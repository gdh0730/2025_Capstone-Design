package com.poseman.backend.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.Valid;
import lombok.Data;

@Data
@Valid
public class ReportResponseDTO {
    private UUID reportId;
    private String storeId;
    private String type;
    private LocalDateTime createdAt;
    private Map<String, Object> summaryData;
}
