package com.poseman.backend.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poseman.backend.domain.entity.Setting;

/**
 * Setting 엔티티를 위한 JPA Repository.
 * - 카메라별 설정을 가져오기 위한 findByCamera 메서드 선언
 */
public interface SettingRepository extends JpaRepository<Setting, UUID> {
    Optional<Setting> findByCamera_CameraId(String cameraId);
}
