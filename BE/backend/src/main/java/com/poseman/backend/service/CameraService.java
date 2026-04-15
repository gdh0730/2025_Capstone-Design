package com.poseman.backend.service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poseman.backend.domain.entity.Camera;
import com.poseman.backend.domain.repository.CameraRepository;
import com.poseman.backend.dto.CameraCreateRequest;
import com.poseman.backend.dto.CameraResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CameraService {
    private final CameraRepository cameraRepository;

    /** 새로운 카메라 등록 */
    @Transactional
    public CameraResponse createCamera(CameraCreateRequest request) {
        // 1) 이미 같은 cameraId가 있는지 확인
        if (cameraRepository.existsById(request.getCameraId())) {
            throw new IllegalArgumentException("Camera already exists: " + request.getCameraId());
        }

        // 2) 엔티티 생성 및 저장
        Camera camera = Camera.builder()
            .cameraId(request.getCameraId())
            .storeId(request.getStoreId())
            .location(request.getLocation())
            .status(true)
            .build();
        Camera saved = cameraRepository.save(camera);

        // 3) 응답용 DTO로 변환
        return CameraResponse.builder()
            .cameraId(saved.getCameraId())
            .storeId(saved.getStoreId())
            .location(saved.getLocation())
            .status(saved.getStatus())
            .createdAt(saved.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .build();
    }

    /** 모든 카메라 조회 (관리용) */
    @Transactional(readOnly = true)
    public List<CameraResponse> getAllCameras() {
        return cameraRepository.findAll().stream().map(cam -> 
            CameraResponse.builder()
                .cameraId(cam.getCameraId())
                .storeId(cam.getStoreId())
                .location(cam.getLocation())
                .status(cam.getStatus())
                .createdAt(cam.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build()
        ).collect(Collectors.toList());
    }

    /** 단일 카메라 조회 */
    @Transactional(readOnly = true)
    public CameraResponse getCamera(String cameraId) {
        Camera cam = cameraRepository.findById(cameraId)
            .orElseThrow(() -> new IllegalArgumentException("Camera not found: " + cameraId));
        return CameraResponse.builder()
            .cameraId(cam.getCameraId())
            .storeId(cam.getStoreId())
            .location(cam.getLocation())
            .status(cam.getStatus())
            .createdAt(cam.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .build();
    }

    /** 카메라 삭제 (관리자 전용) */
    @Transactional
    public void deleteCamera(String cameraId) {
        if (!cameraRepository.existsById(cameraId)) {
            throw new IllegalArgumentException("Camera not found: " + cameraId);
        }
        cameraRepository.deleteById(cameraId);
    }
}
