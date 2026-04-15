package com.poseman.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poseman.backend.dto.CameraCreateRequest;
import com.poseman.backend.dto.CameraResponse;
import com.poseman.backend.service.CameraService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cameras")
@Validated
@RequiredArgsConstructor
public class CameraController {
    private final CameraService cameraService;

    /** 1) 카메라 생성 (관리자 전용) */
    @PostMapping
    public ResponseEntity<CameraResponse> create(@RequestBody @Valid CameraCreateRequest request) {
        CameraResponse response = cameraService.createCamera(request);
        return ResponseEntity.ok(response);
    }

    /** 2) 카메라 목록 조회 (관리자 전용) */
    @GetMapping
    public ResponseEntity<List<CameraResponse>> listAll() {
        List<CameraResponse> list = cameraService.getAllCameras();
        return ResponseEntity.ok(list);
    }

    /** 3) 특정 카메라 조회 (관리자 전용) */
    @GetMapping("/{cameraId}")
    public ResponseEntity<CameraResponse> getOne(@PathVariable String cameraId) {
        CameraResponse cam = cameraService.getCamera(cameraId);
        return ResponseEntity.ok(cam);
    }

    /** 4) 카메라 삭제 (관리자 전용) */
    @DeleteMapping("/{cameraId}")
    public ResponseEntity<Void> delete(@PathVariable String cameraId) {
        cameraService.deleteCamera(cameraId);
        return ResponseEntity.noContent().build();
    }
}
