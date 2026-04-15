package com.poseman.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poseman.backend.domain.entity.Camera;
import com.poseman.backend.domain.entity.Setting;
import com.poseman.backend.domain.repository.CameraRepository;
import com.poseman.backend.domain.repository.SettingRepository;
import com.poseman.backend.dto.SettingUpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final CameraRepository cameraRepository;
    private final SettingRepository settingRepository;

    /**
     * /api/settings/camera/{cameraId} PUT 요청 시 호출
     * 1) 존재하는 카메라인지 확인
     * 2) 기존 Setting 엔티티가 있으면 조회, 없으면 새로 생성
     * 3) 민감도(sensitivity)와 알림 활성화(alertEnabled) 저장
     *
     * @param cameraId - 변경할 대상 카메라 ID
     * @param dto      - { sensitivity, alertEnabled }
     */
    @Transactional
    public void update(String cameraId, SettingUpdateRequest dto) {
        // 1) 카메라 조회
        Camera camera = cameraRepository.findById(cameraId)
                .orElseThrow(() -> new IllegalArgumentException("Camera not found: " + cameraId));

        // 2) 기존 설정 조회 or 새 설정 생성
        Setting setting = settingRepository.findByCamera_CameraId(cameraId)
                .orElseGet(() -> {
                    Setting newSetting = new Setting();
                    newSetting.setCamera(camera);
                    return newSetting;
                });

        // 3) 필드 업데이트 후 저장
        if (dto.getSensitivity() != null) {
            setting.setSensitivity(dto.getSensitivity());
        }
        if (dto.getAlertEnabled() != null) {
            setting.setAlertEnabled(dto.getAlertEnabled());
        }
        settingRepository.save(setting);
    }
}
