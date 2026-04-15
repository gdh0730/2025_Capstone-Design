package com.poseman.backend.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 카메라 정보 (예: cam1, cam2 등).
 */
@Entity
@Table(name = "cameras", indexes = {
        @Index(name = "idx_camera_store_id", columnList = "store_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Camera {

    @Id
    @Column(length = 200)
    private String cameraId; // "cam1", "cam2" 등 고유 ID

    @Column(nullable = false, length = 50)
    private String storeId; // 매장 ID (예: "store3")

    @Column(length = 100)
    private String location; // 설치 위치 (예: "매장 입구", "계산대")

    @Builder.Default
    @Column(nullable = false)
    private Boolean status = true; // 사용 여부 (활성화 또는 비활성화)

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 등록 시각
}
