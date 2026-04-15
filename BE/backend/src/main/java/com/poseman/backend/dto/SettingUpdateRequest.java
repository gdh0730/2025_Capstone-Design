package com.poseman.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SettingUpdateRequest {
    @Min(0)
    @Max(1)
    private Float sensitivity;
    private Boolean alertEnabled;
}
