package com.poseman.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poseman.backend.dto.SettingUpdateRequest;
import com.poseman.backend.service.SettingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @PutMapping("/camera/{cameraId}")
    public ResponseEntity<Void> updateSetting(
            @PathVariable String cameraId,
            @RequestBody SettingUpdateRequest dto) {
        settingService.update(cameraId, dto);
        return ResponseEntity.ok().build();
    }
}
