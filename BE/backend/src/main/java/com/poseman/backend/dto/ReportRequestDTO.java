package com.poseman.backend.dto;

import com.poseman.backend.domain.Enum.Period;

import lombok.Data;

@Data
public class ReportRequestDTO {
    private String storeId;
    private Period period; // "daily" or "weekly"
}
